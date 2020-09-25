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
	 *            The output stream to place things into.
	 */
	public LogStream(PrintStream out) {
		output = out;
		verbosity = FATAL;
	}

	/**
	 * Create a new log stream.
	 *
	 * @param out
	 *              The output stream to place things into.
	 * @param level
	 *              The verbosity level. Use the constants in this class for the
	 *              values.
	 */
	public LogStream(PrintStream out, int level) {
		output = out;
		verbosity = level;
	}

	/**
	 * Create a new log stream.
	 *
	 * Defaults to printing only fatal errors.
	 *
	 * @param out
	 *            The output stream to place things into.
	 */
	public LogStream(OutputStream out) {
		output = new PrintStream(out);
		verbosity = FATAL;
	}

	/**
	 * Create a new log stream.
	 *
	 * @param out
	 *              The output stream to place things into.
	 * @param level
	 *              The verbosity level. Use the constants in this class for the
	 *              values.
	 */
	public LogStream(OutputStream out, int level) {
		output = new PrintStream(out);
		verbosity = level;
	}
	
	/**
	 * Get the verbosity of the stream.
	 * 
	 * @return The verbosity of the stream.
	 */
	public int verbosity() {
		return verbosity;
	}

	/**
	 * Set the verbosity of the stream.
	 * 
	 * @param verb
	 *             The verbosity of the stream.
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
	 * 
	 * @param amt
	 *            The amount to increase the verbosity by.
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
	 * 
	 * @param amt
	 *            The amount to decrease the verbosity by.
	 */
	public void quieter(int amt) {
		verbosity -= amt;
	}

	/**
	 * Print a message that will always be visible.
	 *
	 * @param msg
	 *            The message to print.
	 */
	public void print(String msg) {
		output.print(msg);
	}

	/**
	 * Print a formatted message that will always be visible.
	 *
	 * @param msg
	 *             The format string for the message to print.
	 *
	 * @param args
	 *             The arguments to the format string.
	 */
	public void printf(String msg, Object... args) {
		output.printf(msg, args);
	}

	/**
	 * Print a message at a given verbosity level.
	 * 
	 * @param lvl
	 *            The verbosity level.
	 * @param msg
	 *            The message to print.
	 */
	public void message(int lvl, String msg) {
		if (verbosity >= lvl) {
			output.print(msg);
		}
	}

	/**
	 * Print a formatted message at a given verbosity level.
	 * 
	 * @param lvl
	 *             The verbosity level.
	 * @param msg
	 *             The message to print.
	 * @param args
	 *             The arguments to the message.
	 */
	public void messagef(int lvl, String msg, Object... args) {
		if (verbosity >= lvl) {
			output.printf(msg, args);
		}
	}

	/**
	 * Emit a fatal error message.
	 * 
	 * @param msg
	 *            The message to emit.
	 */
	public void fatal(String msg) {
		message(FATAL, msg);
	}

	/**
	 * Emit a formatted fatal error message.
	 * 
	 * @param msg
	 *             The message to emit.
	 * @param args
	 *             The arguments to the message.
	 */
	public void fatalf(String msg, Object... args) {
		messagef(FATAL, msg, args);
	}

	/**
	 * Emit a normal error message.
	 * 
	 * @param msg
	 *            The message to emit.
	 */
	public void error(String msg) {
		message(ERROR, msg);
	}

	/**
	 * Emit a formatted normal error message.
	 * 
	 * @param msg
	 *             The message to emit.
	 * @param args
	 *             The arguments to the message.
	 */
	public void errorf(String msg, Object... args) {
		messagef(ERROR, msg, args);
	}

	/**
	 * Emit a warning message.
	 * 
	 * @param msg
	 *            The message to emit.
	 */
	public void warn(String msg) {
		message(WARN, msg);
	}

	/**
	 * Emit a formatted warning message.
	 * 
	 * @param msg
	 *             The message to emit.
	 * @param args
	 *             The arguments to the message.
	 */
	public void warnf(String msg, Object... args) {
		messagef(WARN, msg, args);
	}

	/**
	 * Emit an info message.
	 * 
	 * @param msg
	 *            The message to emit.
	 */
	public void info(String msg) {
		message(INFO, msg);
	}

	/**
	 * Emit a formatted info message.
	 * 
	 * @param msg
	 *             The message to emit.
	 * @param args
	 *             The arguments to the message.
	 */
	public void infof(String msg, Object... args) {
		messagef(INFO, msg, args);
	}

	/**
	 * Emit a debug message.
	 * 
	 * @param msg
	 *            The message to emit.
	 */
	public void debug(String msg) {
		message(DEBUG, msg);
	}

	/**
	 * Emit a formatted debug message.
	 * 
	 * @param msg
	 *             The message to emit.
	 * @param args
	 *             The arguments to the message.
	 */
	public void debugf(String msg, Object... args) {
		messagef(DEBUG, msg, args);
	}

	/**
	 * Emit a tracing message.
	 * 
	 * @param msg
	 *            The message to emit.
	 */
	public void trace(String msg) {
		message(TRACE, msg);
	}

	/**
	 * Emit a formatted tracing message.
	 * 
	 * @param msg
	 *             The message to emit.
	 * @param args
	 *             The arguments to the message.
	 */
	public void tracef(String msg, Object... args) {
		messagef(TRACE, msg, args);
	}
}
