package bjc.everge;

import java.util.ArrayList;
import java.util.List;

/**
 * Exception thrown when ReplPair parsing fails
 * 
 * @author bjculkin
 *
 */
public class BadReplParse extends RuntimeException {
	/**
	 * Serialization ID.
	 */
	private static final long serialVersionUID = 4752304282380556849L;
	/**
	 * The errors that were encountered during parsing.
	 */
	public List<ReplPairError> errs;

	/**
	 * Create a new exception for ReplPair parsing failing.
	 *
	 * @param msg
	 *            The message for the exception.
	 */
	public BadReplParse(String msg) {
		this(msg, new ArrayList<>());
	}

	/**
	 * Create a new exception for ReplPair parsing failing.
	 *
	 * @param msg
	 *             The message for the exception.
	 * @param errs
	 *             The list of errors encountered while parsing.
	 */
	public BadReplParse(String msg, List<ReplPairError> errs) {
		super(msg);

		this.errs = errs;
	}

	@Override
	public String toString() {
		String errString;
		if (errs.size() == 0)
			errString = "An error";
		else
			errString = "Errors";

		return String.format("%s occured parsing replacement pairs: %s\n%s", errString,
				getMessage(), errs);
	}

	/**
	 * Convert the exception to a printable format.
	 *
	 * @return The exception as a printable format.
	 */
	public String toPrintString() {
		StringBuilder errString = new StringBuilder("[ERROR] ");

		if (errs.size() == 0) {
			errString.append("No specific errors");
		} else if (errs.size() == 1) {
			errString.append("An error");
		} else {
			errString.append(errs.size());
			errString.append(" errors");
		}

		errString.append(" occured parsing replacement pairs:");
		if (!getMessage().equals("")) {
			errString.append(" ");
			errString.append(getMessage());
		}

		if (errs.size() > 0) {
			errString.append("\n\t");

			for (ReplPairError err : errs) {
				errString.append(err.toPrintString("\t"));
				errString.append("\n\t");
			}
		}

		return errString.toString().trim();
	}
}
