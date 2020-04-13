package bjc.everge;

import java.io.*;

/**
 * Simple class used for logging with various levels.
 *
 * @author Ben Culkin
 */
public class LogStream {
	/**
	 * Log level for printing nothing.
	 */
	public static final int NOTHING = -1;

	/**
	 * Log level for printing only fatal errors.
	 */
	public static final int FATAL = 0;

	/**
	 * Log level for printing all errors.
	 */
	public static final int ERROR = 1;

	/**
	 * Log level for printing warnings.
	 */
	public static final int WARN = 2;

	/**
	 * Log level for printing info messages.
	 */
	public static final int INFO = 3;

	/**
	 * Log level for printing debug messages.
	 */
	public static final int DEBUG = 4;

	/**
	 * Log level for printing trace messages.
	 */
	public static final int TRACE = 5;

	private int verbosity;

	private PrintStream output;

	/**
	 * Create a new log stream.
	 *
	 * Defaults to printing only fatal errors.
	 *
	 * @param out
	 * 	The output stream to place things into.
	 */
	public LogStream(PrintStream out) {
		output = out;
		verbosity = FATAL;
	}

	/**
	 * Create a new log stream.
	 *
	 * @param out
	 * 	The output stream to place things into.
	 * @param level
	 * 	The verbosity level. Use the constants in this class for the values.
	 */
	public LogStream(PrintStream out, int level) {
		output = out;
		verbosity = level;
	}

	/**
	 * Get the verbosity of the stream.
	 * @return The verbosity of the stream.
	 */
	public int verbosity() {
		return verbosity;
	}

	/**
	 * Set the verbosity of the stream.
	 * @param verb The verbosity of the stream.
	 */
	public void verbosity(int verb) {
		verbosity = verb;
	}

	/**
	 * Increment the verbosity of the stream.
	 */
	public void louder() {
		louder(1);
	}

	/**
	 * Increase the verbosity of the stream by an amount.
	 * @param amt The amount to increase the verbosity by.
	 */
	public void louder(int amt) {
		verbosity += amt;
	}
	
	/**
	 * Decrement the verbosity of the stream.
	 */
	public void quieter() {
		quieter(1);
	}

	/**
	 * Decrease the verbosity of the stream by an amount.
	 * @param amt The amount to decrease the verbosity by.
	 */
	public void quieter(int amt) {
		verbosity -= amt;
	}

	/**
	 * Print a message that will always be visible.
	 *
	 * @param msg
	 * 	The message to print.
	 */
	public void print(String msg) {
		output.print(msg);
	}

	/**
	 * Print a formatted message that will always be visible.
	 *
	 * @param msg
	 * 	The format string for the message to print.
	 *
	 * @param args
	 * 	The arguments to the format string.
	 */
	public void printf(String msg, Object... args) {
		output.printf(msg, args);
	}

	/**
	 * Print a message at a given verbosity level.
	 * @param lvl The verbosity level.
	 * @param msg The message to print.
	 */
	public void message(int lvl, String msg) {
		if (verbosity >= lvl) {
			output.print(msg);
		}
	}

	public void messagef(int lvl, String msg, Object... args) {
		if (verbosity >= lvl) {
			output.printf(msg, args);
		}
	}

	public void fatal(String msg) {
		message(FATAL, msg);
	}

	public void fatalf(String msg, Object... args) {
		messagef(FATAL, msg, args);
	}

	public void error(String msg) {
		message(ERROR, msg);
	}

	public void errorf(String msg, Object... args) {
		messagef(ERROR, msg, args);
	}

	public void warn(String msg) {
		message(WARN, msg);
	}

	public void warnf(String msg, Object... args) {
		messagef(WARN, msg, args);
	}

	public void info(String msg) {
		message(INFO, msg);
	}

	public void infof(String msg, Object... args) {
		messagef(INFO, msg, args);
	}

	public void debug(String msg) {
		message(DEBUG, msg);
	}

	public void debugf(String msg, Object...args) {
		messagef(DEBUG, msg, args);
	}

	public void trace(String msg) {
		message(TRACE, msg);
	}

	public void tracef(String msg, Object... args) {
		messagef(TRACE, msg, args);
	}
}
