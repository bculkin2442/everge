package bjc.everge;

import java.io.*;
import java.util.*;

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

	public int verbosity() {
		return verbosity;
	}

	public void verbosity(int verb) {
		verbosity = verb;
	}

	public void louder() {
		louder(1);
	}

	public void louder(int amt) {
		verbosity += amt;
	}

	public void quieter() {
		quieter(1);
	}

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
