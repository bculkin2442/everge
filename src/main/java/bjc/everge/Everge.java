package bjc.everge;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import java.nio.charset.Charset;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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

	// Loaded repl-pairs
	private List<ReplPair> lrp = new ArrayList<>();

	// Input status
	private InputStatus inputStat = InputStatus.ALL;

	// Are we processing CLI args? (haven't seen a -- yet)
	private boolean doingArgs = true;

	// Should an NL be printed after each replace?
	private boolean printNL = true;

	// Verbosity level
	private int verbosity = 0;

	// The pattern to use for REGEX input mode
	private String pattern;

	// The queue of arguments to process
	private Deque<String> argQue = new LinkedList<>();

	// Used to prevent inter-mixing argument alterations with input processing.
	private ReadWriteLock argLock = new ReentrantReadWriteLock();

	// Input/output streams
	public PrintStream outStream = System.out;
	public PrintStream errStream = System.err;

	/**
	 * Main method for front end,
	 *
	 * @param args
	 * 		The CLI arguments.
	 */
	public static void main(String[] args) {
		Everge evg = new Everge();

		evg.processArgs(args);
	}

	/**
	 * Process one or more command line arguments.
	 *
	 * @param args
	 * 		The arguments to process.
	 * @return Whether we processed succesfully or not.
	 */
	public boolean processArgs(String... args) {
		List<String> errs = new ArrayList<>();

		boolean stat = processArgs(errs, args);
		if (!stat) {
			for (String err : errs) {
				errStream.println(err);
			}
		}

		return stat;
	}

	/**
	 * Process one or more command line arguments.
	 *
	 * @param args
	 * 		The arguments to process.
	 * @param errs
	 * 		The list to stash errors in.
	 * @return Whether we processed succesfully or not.
	 */
	public boolean processArgs(List<String> errs, String... args) {
		argLock.writeLock().lock();

		boolean retStat = true;

		try {
			loadQueue(args);

			// Process CLI args
			while(argQue.size() > 0) {
				String arg = argQue.pop();

				if (arg.equals("--")) {
					doingArgs = false;
					continue;
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
						break;
					case "-q":
					case "--quiet":
						verbosity -= 1;
						break;
					case "--verbosity":
						if (argQue.size() < 1) {
							errs.add("[ERROR] No parameter to --verbosity");
							retStat = false;
							break;
						}
						argBody = argQue.pop();
						break;
					case "-V":
						try {
							verbosity = Integer.parseInt(argBody);
						} catch (NumberFormatException nfex) {
							String msg = String.format("[ERROR] Invalid verbosity: '%s' is not an integer",
									argBody);
							errs.add(msg);
							retStat = false;
						}
						break;
					case "--pattern":
						if (argQue.size() < 1) {
							errs.add("[ERROR] No parameter to --pattern");
							retStat = false;
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
							retStat = false;
						}
						break;
					case "--file":
						if (argQue.size() < 1) {
							errs.add("[ERROR] No argument to --file");
							retStat = false;
							break;
						}
						argBody = argQue.pop();
					case "-f":
						try (FileInputStream fis = new FileInputStream(argBody);
								Scanner scn = new Scanner(fis)) {
							List<ReplError> ferrs = new ArrayList<>();

							lrp = ReplPair.readList(lrp, scn, ferrs, ropts);

							if (ferrs.size() > 0) {
								StringBuilder sb = new StringBuilder();
								
								String errString = "an error";
								if (ferrs.size() > 1) errString = String.format("%d errors");

								{
									String msg = String.format(
											"[ERROR] Encountered errors parsing data file'%s'\n",
											argBody);
									sb.append(msg);
								}

								for (ReplError err : ferrs) {
									sb.append(String.format("\t%s\n", err));
								}

								errs.add(sb.toString());
								retStat = false;
							}
						} catch (FileNotFoundException fnfex) {
							String msg = String.format("[ERROR] Could not open data file '%s' for input",
									argBody);
							errs.add(msg);
							retStat = false;
						} catch (IOException ioex) {
							String msg = String.format("[ERROR] Unknown I/O error reading data file '%s': %s",
									argBody, ioex.getMessage());
							errs.add(msg);
							retStat = false;
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

								if (ln.equals(""))      continue;
								if (ln.startsWith("#")) continue;

								sl.add(ln);
							}

							processArgs(sl.toArray(new String[0]));
						} catch (FileNotFoundException fnfex) {
							String msg = String.format("[ERROR] Could not open argument file '%s' for input", argBody);
							errs.add(msg);
							retStat = false;
						} catch (IOException ioex) {
							String msg = String.format("[ERROR] Unknown I/O error reading input file '%s': %s",
									argBody, ioex.getMessage());
							errs.add(msg);
							retStat = false;
						}
						break;
					default:
						{
							String msg = String.format("[ERROR] Unrecognised CLI argument name '%s'\n", argName);
							errs.add(msg);
							retStat = false;
						}
					}
				} else {
					// Strip off an escaped initial dash
					if (arg.startsWith("\\-")) arg = arg.substring(1);

					processInputFile(arg);
				}
			}
		} finally {
			argLock.writeLock().unlock();
		}

		return retStat;
	}

	/**
	 * Process a input file.
	 *
	 * @param fle
	 * 		Input file to process.
	 * @return Whether we processed succesfully or not.
	 */
	public boolean processInputFile(String fle) {
		List<String> errs = new ArrayList<>();

		boolean stat = processInputFile(errs, fle);
		if (!stat) {
			for (String err : errs) {
				errStream.println(err);
			}
		}

		return stat;
	}

	/**
	 * Process a input file.
	 *
	 * @param fle
	 * 		Input file to process.
	 * @param errs
	 * 		List to accumulate errors in.
	 * @return Whether we processed succesfully or not.
	 */
	public boolean processInputFile(List<String> errs, String fle) {
		argLock.readLock().lock();

		// Read in and do replacements on a file
		try {
			if (verbosity > 2) {
				errStream.printf("[TRACE] Reading file (%s) in mode (%s)\n", fle, inputStat);
			}

			if (inputStat == InputStatus.ALL) {
				Path pth = Paths.get(fle);

				if (!Files.isReadable(pth)) {
					String msg = String.format("[ERROR] File '%s' is not readable\n", fle);
					errs.add(msg);
					return false;
				} else {
					byte[] inp = Files.readAllBytes(pth);

					String strang = new String(inp, Charset.forName("UTF-8"));

					processString(strang);
				}
			} else if (inputStat == InputStatus.LINE) {
				try (FileInputStream fis = new FileInputStream(fle); Scanner scn = new Scanner(fis)) {
					while(scn.hasNextLine()) {
						processString(scn.nextLine());
					}
				}
			} else if (inputStat == InputStatus.REGEX) {
				try (FileInputStream fis = new FileInputStream(fle); Scanner scn = new Scanner(fis)) {
					scn.useDelimiter(pattern);

					while(scn.hasNext()) {
						processString(scn.next());
					}
				}
			} else {
				String msg = String.format("[INTERNAL-ERROR] Input status '%s' is not yet implemented\n",
						inputStat);
				errs.add(msg);
				return false;
			}
		} catch (IOException ioex) {
			String msg = String.format("[ERROR] Unknown I/O related error for file '%s'\n\tError was %s",
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
	 * 		The input string to process.
	 */
	public void processString(String inp) {
		argLock.readLock().lock();

		try {
			String strang = inp;

			for (ReplPair rp : lrp) {
				strang = rp.apply(strang);
			}

			outStream.print(strang);
			if (printNL) outStream.println();
		} finally {
			argLock.readLock().unlock();
		}
	}

	// Load arguments into the argument queue.
	private void loadQueue(String... args) {
		boolean doArgs = true;
		for (String arg : args) {
			if (arg.equals("--")) doArgs = false;

			// Handle things like -nNv correctly
			if (doArgs) {
				if (arg.startsWith("-") && !arg.startsWith("--")) {
					char[] car = arg.substring(1).toCharArray();
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
