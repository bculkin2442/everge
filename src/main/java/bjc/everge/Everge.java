package bjc.everge;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.locks.*;
import java.util.regex.*;

import bjc.utils.ioutils.*;

/**
 * Everge front-end application.
 *
 * @author Ben Culkin
 */
public class Everge {
	/**
	 * Details how we handle our input.
	 */
	public static enum InputStatus {
		/**
		 * Process the input as a single string.
		 */
		ALL,
		/**
		 * Process the input line-by-line.
		 */
		LINE,
		/**
		 * Process the input, splitting it around occurrences of a regular expression.
		 */
		REGEX;
	}

	/**
	 *  Options for doing repl-pairs
	 */
	private ReplPairOptions replOptions = new ReplPairOptions();

	/**
	 * Repository for ReplPairs
	 */
	private ReplPairSet replSet = new ReplPairSet();

	/**
	 *  Input status.
	 *  
	 *  How the input to run replacements on should be processed.
	 */
	private InputStatus inputStatus = InputStatus.ALL;

	/**
	 *  Are we processing CLI args? (haven't seen a -- yet)
	 */
	private boolean doingArgs = true;

	/**
	 *  Should an NL be printed after each replace?
	 */
	private boolean printNLAfterReplace = true;

	/**
	 *  Verbosity level
	 */
	private int verbosity;

	/**
	 *  The pattern to use for REGEX input mode
	 */
	private String regexPattern;

	/**
	 *  Used to prevent inter-mixing argument alterations with input processing.
	 */
	private ReadWriteLock argLock = new ReentrantReadWriteLock();

	// Input/output streams
	/**
	 * Stream to use for normal output.
	 */
	private PrintStream outputStream = System.out;
	/**
	 * Stream to use for error output.
	 */
	private LogStream errorStream = new LogStream(System.err);

	/**
	 * Set the output stream.
	 * 
	 * @param out
	 *            The output stream..
	 */
	public void setOutput(PrintStream out) {
		outputStream = out;
	}

	/**
	 * Set the output stream.
	 * 
	 * @param out
	 *            The output stream..
	 */
	public void setOutput(OutputStream out) {
		setOutput(new PrintStream(out));
	}

	/**
	 * Set the error stream.
	 * 
	 * @param err
	 *            The error stream.
	 */
	public void setError(PrintStream err) {
		errorStream = new LogStream(err);
	}

	/**
	 * Set the error stream.
	 * 
	 * @param err
	 *            The error stream.
	 */
	public void setError(OutputStream err) {
		setError(new PrintStream(err));
	}

	/**
	 * Main method for front end,
	 *
	 * @param args
	 *             The CLI arguments.
	 */
	public static void main(String[] args) {
		Everge evg = new Everge();

		evg.processArgs(args);
	}

	/**
	 * Process one or more command line arguments.
	 *
	 * @param args
	 *             The arguments to process.
	 * @return Whether we processed successfully or not.
	 */
	public boolean processArgs(String... args) {
		List<String> errs = new ArrayList<>();

		boolean stat = processArgs(errs, args);
		if (verbosity >= 2) {
			String argString = args.length > 0 ? "arguments" : "argument";

			errorStream.infof("[INFO] Processed %d %s\n", args.length, argString);
			int argCount = 0;
			if (verbosity >= 3) {
				String arg = args[argCount++];
				errorStream.tracef("[TRACE]\tArg %d: '%s\n", argCount, arg);
			}
		}

		if (!stat)
			for (String err : errs) errorStream.errorf("%s\n", err);

		return stat;
	}

	/**
	 * Process one or more command line arguments.
	 *
	 * @param args
	 *             The arguments to process.
	 * @param errs
	 *             The list to stash errors in.
	 * @return Whether we processed successfully or not.
	 */
	public boolean processArgs(List<String> errs, String... args) {
		argLock.writeLock().lock();

		boolean returnStatus = true;

		try {
			Deque<String> argQueue = loadQueue(args);

			// Process CLI arguments
			while (argQueue.size() > 0) {
				String arg = argQueue.pop();

				returnStatus = processArg(errs, returnStatus, arg, argQueue);
			}
		} finally {
			argLock.writeLock().unlock();
		}

		return returnStatus;
	}

	private boolean processArg(List<String> errors, boolean retStat, String arg, Deque<String> argQueue) {
		boolean newReturnStatus = retStat;

		if (arg.equals("--")) {
			doingArgs = false;
			
			return newReturnStatus;
		}

		// Process an argument
		if (doingArgs && arg.startsWith("-")) {
			String argName = arg;
			String argBody = "";

			// Process 'joined' arguments (a=b)
			int idx = arg.indexOf("=");
			if (idx != -1) {
				argName = arg.substring(0, idx);
				argBody = arg.substring(idx + 1);
			}

			switch (argName) {
			case "-n":
			case "--newline":
				printNLAfterReplace = true;
				break;
			case "-N":
			case "--no-newline":
			case "--nonewline":
				printNLAfterReplace = false;
				break;
				
			case "-v":
			case "--verbose":
				verbosity += 1;
				errorStream.louder();
				//System.err.printf("[TRACE] Incremented verbosity\n");
				break;
			case "-q":
			case "--quiet":
				verbosity -= 1;
				errorStream.quieter();
				//System.err.printf("[TRACE] Decremented verbosity\n");
				break;
			case "--verbosity":
				if (argQueue.size() < 1) {
					errors.add("[ERROR] No parameter to --verbosity");
					newReturnStatus = false;
					break;
				}
				argBody = argQueue.pop();
			case "-V":
				try {
					verbosity = Integer.parseInt(argBody);
					errorStream.verbosity(verbosity);
					//System.err.printf("[TRACE] Set verbosity to %d\n", verbosity);
				} catch (NumberFormatException nfex) {
					String msg = String.format(
							"[ERROR] Invalid verbosity: '%s' is not an integer", argBody);
					errors.add(msg);
					newReturnStatus = false;
				}
				break;
				
			case "--pattern":
				if (argQueue.size() < 1) {
					errors.add("[ERROR] No parameter to --pattern");
					newReturnStatus = false;
					break;
				}
				argBody = argQueue.pop();
			case "-p":
				if (inputStatus != InputStatus.REGEX) 
					errorStream.warn("[WARN] Specified pattern will be ignored unless input mode is switched to REGEX");
				
				try {
					regexPattern = argBody;

					Pattern.compile(argBody);
				} catch (PatternSyntaxException psex) {
					String msg = String.format("[ERROR] Pattern '%s' is invalid: %s",
							regexPattern, psex.getMessage());
					errors.add(msg);
					newReturnStatus = false;
				}
				break;
				
			case "--file":
				if (argQueue.size() < 1) {
					errors.add("[ERROR] No argument to --file");
					newReturnStatus = false;
					break;
				}
				argBody = argQueue.pop();
			case "-f":
				try (FileInputStream fis = new FileInputStream(argBody);
						Scanner scn = new Scanner(fis)) {
					List<ReplPairError> ferrs = new ArrayList<>();

					List<ReplPair> pairList = new ArrayList<>();
					ReplPairParser parser = new ReplPairParser();
					pairList = parser.readList(pairList, scn, ferrs, replOptions);

					if (ferrs.size() > 0) {
						StringBuilder sb = new StringBuilder();

						String errString = "an error";
						if (ferrs.size() > 1)
							errString = String.format("%d errors", ferrs.size());

						
						String msg = String.format(
								"[ERROR] Encountered %s parsing data file'%s'\n",
								errString, argBody);
						sb.append(msg);
						
						for (ReplPairError err : ferrs)
							sb.append(String.format("\t%s\n", err));

						errors.add(sb.toString());
						newReturnStatus = false;
					}

					replSet.addPairs(pairList);
				} catch (FileNotFoundException fnfex) {
					String msg = String.format(
							"[ERROR] Could not open data file '%s' for input", argBody);
					errors.add(msg);
					newReturnStatus = false;
				} catch (IOException ioex) {
					String msg = String.format(
							"[ERROR] Unknown I/O error reading data file '%s': %s",
							argBody, ioex.getMessage());
					errors.add(msg);
					newReturnStatus = false;
				}
				break;
				
			case "--arg-file":
				if (argQueue.size() < 1) {
					errors.add("[ERROR] No argument to --arg-file");
					break;
				}
				argBody = argQueue.pop();
			case "-F":
				try (FileInputStream fis = new FileInputStream(argBody);
						Scanner scn = new Scanner(fis)) {
					List<String> sl = new ArrayList<>();

					while (scn.hasNextLine()) {
						String ln = scn.nextLine().trim();

						if (ln.equals(""))      continue;
						if (ln.startsWith("#")) continue;

						sl.add(ln);
					}

					// @FixMe :ArgFile
					// This won't work properly when using the 'non-inline' arguments
					// oops. It should. -- bculkin, Oct 31
					processArgs(sl.toArray(new String[0]));
				} catch (FileNotFoundException fnfex) {
					String msg = String.format(
							"[ERROR] Could not open argument file '%s' for input",
							argBody);
					errors.add(msg);
					newReturnStatus = false;
				} catch (IOException ioex) {
					String msg = String.format(
							"[ERROR] Unknown I/O error reading input file '%s': %s",
							argBody, ioex.getMessage());
					errors.add(msg);
					newReturnStatus = false;
				}
				break;
				
			case "--input-status":
				if (argQueue.size() < 1) {
					errors.add("[ERROR] No argument to --input-status");
					break;
				}
				argBody = argQueue.pop();
			case "-I":
				try {
					inputStatus = InputStatus.valueOf(argBody.toUpperCase());
				} catch (IllegalArgumentException iaex) {
					String msg = String.format("[ERROR] '%s' is not a valid input status",
							argBody);
					errors.add(msg);
				}
				break;
				
			default: {
				String msg = String.format(
						"[ERROR] Unrecognised CLI argument name '%s'\n", argName);
				errors.add(msg);
				newReturnStatus = false;
			}
			}
		} else {
			String tmp = arg;
			// Strip off an escaped initial dash
			if (tmp.startsWith("\\-")) tmp = tmp.substring(1);

			processInputFile(tmp);
		}

		return newReturnStatus;
	}

	/**
	 * Process a input file.
	 *
	 * @param fle
	 *            Input file to process.
	 * @return Whether we processed succesfully or not.
	 */
	public boolean processInputFile(String fle) {
		List<String> errs = new ArrayList<>();

		boolean stat = processInputFile(errs, fle);
		if (!stat) for (String err : errs) errorStream.errorf("%s\n", err);

		return stat;
	}

	/**
	 * Process a input file.
	 *
	 * @param fle
	 *             Input file to process.
	 * @param errs
	 *             List to accumulate errors in.
	 * @return Whether we processed succesfully or not.
	 */
	public boolean processInputFile(List<String> errs, String fle) {
		argLock.readLock().lock();

		// Read in and do replacements on a file
		try {
			if (verbosity > 2) {
				errorStream.printf("[TRACE] Reading file (%s) in mode (%s)\n", fle,
						inputStatus);
			}

			if (inputStatus == InputStatus.ALL) {
				Path pth = Paths.get(fle);

				if (!Files.isReadable(pth)) {
					String msg = String.format("[ERROR] File '%s' is not readable\n", fle);
					errs.add(msg);
					return false;
				}

				byte[] inp = Files.readAllBytes(pth);

				String strang = new String(inp, Charset.forName("UTF-8"));

				processString(strang);
			} else if (inputStatus == InputStatus.LINE) {
				try (
						FileInputStream fis = new FileInputStream(fle);
						Scanner scn = new Scanner(fis)) {
					while (scn.hasNextLine()) processString(scn.nextLine());
				}
			} else if (inputStatus == InputStatus.REGEX) {
				try (FileInputStream fis = new FileInputStream(fle);
						Scanner scn = new Scanner(fis)) {
					scn.useDelimiter(regexPattern);

					while (scn.hasNext()) processString(scn.next());
				}
			} else {
				String msg = String.format(
						"[INTERNAL-ERROR] Input status '%s' is not yet implemented\n",
						inputStatus);
				errs.add(msg);
				return false;
			}
		} catch (IOException ioex) {
			String msg = String.format(
					"[ERROR] Unknown I/O related error for file '%s'\n\tError was %s",
					fle, ioex.getMessage());
			errs.add(msg);
			return false;
		} finally {
			argLock.readLock().unlock();
		}

		return true;
	}

	/**
	 * Process an input string.
	 *
	 * @param inp
	 *            The input string to process.
	 */
	public void processString(String inp) {
		argLock.readLock().lock();

		try {
			String strang = inp;

			if (verbosity >= 3) {
				errorStream.infof(
						"[INFO] Processing replacements for string '%s' in mode %s\n",
						strang, inputStatus);
				
				if (!inp.equals(inp.trim())) {
					errorStream.infof("[INFO] String '%s' has trailing spaces on it\n", inp);
				}
			}

			strang = replSet.apply(inp);

			outputStream.print(strang);
			if (printNLAfterReplace) outputStream.println();
		} finally {
			argLock.readLock().unlock();
		}
	}

	// Load arguments into the argument queue.
	private Deque<String> loadQueue(String... args) {
		Deque<String> argQueue = new ArrayDeque<>(args.length);
		
		boolean doArgs = true;
		for (String arg : args) {
			if (arg.equals("--")) {
				doArgs = false;
				continue;
			}

			if (doArgs) {
				if (arg.startsWith("-") && !arg.startsWith("--")) {
					// Handle things like -nNv correctly
					char[] car = arg.substring(1).toCharArray();

					if (verbosity >= 3) {
						errorStream.infof("[INFO] Adding collection of single-char arguments: %s", car);
					}

					for (char c : car) {
						String argstr = String.format("-%c", c);
						argQueue.add(argstr);
					}
				} else {
					argQueue.add(arg);
				}
			} else {
				argQueue.add(arg);
			}
		}
		
		return argQueue;
	}
}
