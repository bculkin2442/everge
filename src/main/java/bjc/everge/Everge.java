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
		 * Process the input, splitting it around occurances of a regex.
		 */
		REGEX;
	}

	// Options for doing repl-pairs
	private ReplOpts ropts = new ReplOpts();

	// Pair repository
	private ReplSet replSet = new ReplSet();

	// Input status
	private InputStatus inputStat = InputStatus.ALL;

	// Are we processing CLI args? (haven't seen a -- yet)
	private boolean doingArgs = true;

	// Should an NL be printed after each replace?
	private boolean printNL = true;

	// Verbosity level
	private int verbosity;

	// The pattern to use for REGEX input mode
	private String pattern;

	// The queue of arguments to process
	private Deque<String> argQue = new LinkedList<>();

	// Used to prevent inter-mixing argument alterations with input processing.
	private ReadWriteLock argLock = new ReentrantReadWriteLock();

	// Input/output streams
	/**
	 * Stream to use for normal output.
	 */
	private PrintStream outStream = System.out;
	/**
	 * Stream to use for error output.
	 */
	private LogStream errStream = new LogStream(System.err);

	/**
	 * Set the output stream.
	 * 
	 * @param out
	 *            The output stream..
	 */
	public void setOutput(PrintStream out) {
		outStream = out;
	}

	/**
	 * Set the output stream.
	 * 
	 * @param out
	 *            The output stream..
	 */
	public void setOutput(OutputStream out) {
		outStream = new PrintStream(out);
	}

	/**
	 * Set the error stream.
	 * 
	 * @param err
	 *            The error stream.
	 */
	public void setError(PrintStream err) {
		errStream = new LogStream(err);
	}

	/**
	 * Set the error stream.
	 * 
	 * @param err
	 *            The error stream.
	 */
	public void setError(OutputStream err) {
		errStream = new LogStream(new PrintStream(err));
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
	 * @return Whether we processed succesfully or not.
	 */
	public boolean processArgs(String... args) {
		List<String> errs = new ArrayList<>();

		boolean stat = processArgs(errs, args);
		if (verbosity >= 2) {
			String argString = args.length > 0 ? "arguments" : "argument";

			errStream.infof("[INFO] Processed %d %s\n", args.length, argString);
			int argc = 0;
			if (verbosity >= 3) {
				String arg = args[argc++];
				errStream.tracef("[TRACE]\tArg %d: '%s\n", argc, arg);
			}
		}

		if (!stat) {
			for (String err : errs) {
				errStream.errorf("%s\n", err);
			}
		}

		return stat;
	}

	/**
	 * Process one or more command line arguments.
	 *
	 * @param args
	 *             The arguments to process.
	 * @param errs
	 *             The list to stash errors in.
	 * @return Whether we processed succesfully or not.
	 */
	public boolean processArgs(List<String> errs, String... args) {
		argLock.writeLock().lock();

		boolean retStat = true;

		try {
			loadQueue(args);

			// Process CLI args
			while (argQue.size() > 0) {
				String arg = argQue.pop();

				retStat = processArg(errs, retStat, arg);
			}
		} finally {
			argLock.writeLock().unlock();
		}

		return retStat;
	}

	private boolean processArg(List<String> errs, boolean retStat, String arg) {
		boolean newRet = retStat;

		if (arg.equals("--")) {
			doingArgs = false;
			return newRet;
		}

		// Process an argument
		if (doingArgs && arg.startsWith("-")) {
			String argName = arg;
			String argBody = "";

			// Process arguments to arguments
			int idx = arg.indexOf("=");
			if (idx != -1) {
				argName = arg.substring(0, idx);
				argBody = arg.substring(idx + 1);
			}

			switch (argName) {
			case "-n":
			case "--newline":
				printNL = true;
				break;
			case "-N":
			case "--no-newline":
				printNL = false;
				break;
			case "-v":
			case "--verbose":
				verbosity += 1;
				errStream.louder();
				System.err.printf("[TRACE] Incremented verbosity\n");
				break;
			case "-q":
			case "--quiet":
				verbosity -= 1;
				errStream.quieter();
				System.err.printf("[TRACE] Decremented verbosity\n");
				break;
			case "--verbosity":
				if (argQue.size() < 1) {
					errs.add("[ERROR] No parameter to --verbosity");
					newRet = false;
					break;
				}
				argBody = argQue.pop();
			case "-V":
				try {
					verbosity = Integer.parseInt(argBody);
					errStream.verbosity(verbosity);
					System.err.printf("[TRACE] Set verbosity to %d\n", verbosity);
				} catch (NumberFormatException nfex) {
					String msg = String.format(
							"[ERROR] Invalid verbosity: '%s' is not an integer", argBody);
					errs.add(msg);
					newRet = false;
				}
				break;
			case "--pattern":
				if (argQue.size() < 1) {
					errs.add("[ERROR] No parameter to --pattern");
					newRet = false;
					break;
				}
				argBody = argQue.pop();
			case "-p":
				try {
					pattern = argBody;

					Pattern.compile(argBody);
				} catch (PatternSyntaxException psex) {
					String msg = String.format("[ERROR] Pattern '%s' is invalid: %s",
							pattern, psex.getMessage());
					errs.add(msg);
					newRet = false;
				}
				break;
			case "--file":
				if (argQue.size() < 1) {
					errs.add("[ERROR] No argument to --file");
					newRet = false;
					break;
				}
				argBody = argQue.pop();
			case "-f":
				try (FileInputStream fis = new FileInputStream(argBody);
						Scanner scn = new Scanner(fis)) {
					List<ReplError> ferrs = new ArrayList<>();

					List<ReplPair> lrp = new ArrayList<>();
					lrp = ReplPair.readList(lrp, scn, ferrs, ropts);

					if (ferrs.size() > 0) {
						StringBuilder sb = new StringBuilder();

						String errString = "an error";
						if (ferrs.size() > 1)
							errString = String.format("%d errors", ferrs.size());

						{
							String msg = String.format(
									"[ERROR] Encountered %s parsing data file'%s'\n",
									errString, argBody);
							sb.append(msg);
						}

						for (ReplError err : ferrs) {
							sb.append(String.format("\t%s\n", err));
						}

						errs.add(sb.toString());
						newRet = false;
					}

					replSet.addPairs(lrp);
				} catch (FileNotFoundException fnfex) {
					String msg = String.format(
							"[ERROR] Could not open data file '%s' for input", argBody);
					errs.add(msg);
					newRet = false;
				} catch (IOException ioex) {
					String msg = String.format(
							"[ERROR] Unknown I/O error reading data file '%s': %s",
							argBody, ioex.getMessage());
					errs.add(msg);
					newRet = false;
				}
				break;
			case "--arg-file":
				if (argQue.size() < 1) {
					errs.add("[ERROR] No argument to --arg-file");
					break;
				}
				argBody = argQue.pop();
			case "-F":
				try (FileInputStream fis = new FileInputStream(argBody);
						Scanner scn = new Scanner(fis)) {
					List<String> sl = new ArrayList<>();

					while (scn.hasNextLine()) {
						String ln = scn.nextLine().trim();

						if (ln.equals(""))
							continue;
						if (ln.startsWith("#"))
							continue;

						sl.add(ln);
					}

					processArgs(sl.toArray(new String[0]));
				} catch (FileNotFoundException fnfex) {
					String msg = String.format(
							"[ERROR] Could not open argument file '%s' for input",
							argBody);
					errs.add(msg);
					newRet = false;
				} catch (IOException ioex) {
					String msg = String.format(
							"[ERROR] Unknown I/O error reading input file '%s': %s",
							argBody, ioex.getMessage());
					errs.add(msg);
					newRet = false;
				}
				break;
			case "--input-status":
				if (argQue.size() < 1) {
					errs.add("[ERROR] No argument to --input-status");
					break;
				}
				argBody = argQue.pop();
			case "-I":
				try {
					inputStat = InputStatus.valueOf(argBody.toUpperCase());
				} catch (IllegalArgumentException iaex) {
					String msg = String.format("[ERROR] '%s' is not a valid input status",
							argBody);
					errs.add(msg);
				}
				break;
			default: {
				String msg = String
						.format("[ERROR] Unrecognised CLI argument name '%s'\n", argName);
				errs.add(msg);
				newRet = false;
			}
			}
		} else {
			String tmp = arg;
			// Strip off an escaped initial dash
			if (tmp.startsWith("\\-"))
				tmp = tmp.substring(1);

			processInputFile(tmp);
		}

		return newRet;
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
		if (!stat) {
			for (String err : errs) {
				errStream.errorf("%s\n", err);
			}
		}

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
				errStream.printf("[TRACE] Reading file (%s) in mode (%s)\n", fle,
						inputStat);
			}

			if (inputStat == InputStatus.ALL) {
				Path pth = Paths.get(fle);

				if (!Files.isReadable(pth)) {
					String msg
							= String.format("[ERROR] File '%s' is not readable\n", fle);
					errs.add(msg);
					return false;
				}

				byte[] inp = Files.readAllBytes(pth);

				String strang = new String(inp, Charset.forName("UTF-8"));

				processString(strang);
			} else if (inputStat == InputStatus.LINE) {
				try (FileInputStream fis = new FileInputStream(fle);
						Scanner scn = new Scanner(fis)) {
					while (scn.hasNextLine()) {
						processString(scn.nextLine());
					}
				}
			} else if (inputStat == InputStatus.REGEX) {
				try (FileInputStream fis = new FileInputStream(fle);
						Scanner scn = new Scanner(fis)) {
					scn.useDelimiter(pattern);

					while (scn.hasNext()) {
						processString(scn.next());
					}
				}
			} else {
				String msg = String.format(
						"[INTERNAL-ERROR] Input status '%s' is not yet implemented\n",
						inputStat);
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
				errStream.infof(
						"[INFO] Processing replacements for string '%s' in mode %s\n",
						strang, inputStat);
				
				if (!inp.equals(inp.trim())) {
					errStream.infof("[INFO] String '%s' has trailing spaces on it\n", inp);
				}
			}

			strang = replSet.apply(inp);

			outStream.print(strang);
			if (printNL)
				outStream.println();
		} finally {
			argLock.readLock().unlock();
		}
	}

	// Load arguments into the argument queue.
	private void loadQueue(String... args) {
		boolean doArgs = true;
		for (String arg : args) {
			if (arg.equals("--")) {
				doArgs = false;
			}

			// Handle things like -nNv correctly
			if (doArgs) {
				if (arg.startsWith("-") && !arg.startsWith("--")) {
					char[] car = arg.substring(1).toCharArray();

					if (verbosity >= 3) {
						errStream.infof("[INFO] Adding stream of args: %s", car);
					}

					for (char c : car) {
						String argstr = String.format("-%c", c);
						argQue.add(argstr);
					}
				} else {
					argQue.add(arg);
				}
			} else {
				argQue.add(arg);
			}
		}
	}
}
