package bjc.everge;

import bjc.data.IntHolder;

/**
 * Represents an error encountered parsing ReplPairs
 *
 * @author Ben Culkin
 */
public class ReplError {
	/**
	 * The line the error occurred on.
	 */
	public int line;
	/**
	 * The number of pairs we have processed so far.
	 */
	public int numPairs;

	/**
	 * The text of the line we errored on.
	 */
	public String txt;
	/**
	 * The message of the error.
	 */
	public String msg;

	/**
	 * Create a new ReplPair parse error.
	 *
	 * @param lne
	 *               The line the error occured on.
	 * @param nPairs
	 *               The number of pairs processed up to this point.
	 * @param msg
	 *               The message detailing the error.
	 * @param txt
	 *               The text that caused the error.
	 */
	public ReplError(IntHolder lne, IntHolder nPairs, String msg, String txt) {
		this(lne.get(), nPairs.get(), msg, txt);
	}

	/**
	 * Create a new ReplPair parse error.
	 *
	 * @param lne
	 *               The line the error occured on.
	 * @param nPairs
	 *               The number of pairs processed up to this point.
	 * @param msg
	 *               The message detailing the error.
	 * @param txt
	 *               The text that caused the error.
	 */
	public ReplError(int lne, int nPairs, String msg, String txt) {
		line = lne;
		numPairs = nPairs;

		this.txt = txt;
		this.msg = msg;
	}

	@Override
	public String toString() {
		String errString;
		if (txt == null)
			errString = "No associated line";
		else if (txt.equals(""))
			errString = "Text of line was empty";
		else
			errString = "Text of line was: " + txt;

		return String.format("line %d, pair %d:%s\n\t%s", line, numPairs, msg, errString);
	}

	/**
	 * Convert the error to a printable string.
	 *
	 * @return The error as a printable string.
	 */
	public String toPrintString() {
		return toPrintString("");
	}

	/**
	 * Convert the error to a printable string, with a custom header.
	 *
	 * @param hdr
	 *            The text to include in the header.
	 *
	 * @return The error as a printable string.
	 */
	public String toPrintString(String hdr) {
		String errString;
		if (txt == null)
			errString = "No associated line";
		else if (txt.equals(""))
			errString = "Text of line was empty";
		else
			errString = "Text of line was: " + txt;

		return String.format("[ERROR] line %d, pair %d: %s\n%s\tContext: %s", line,
				numPairs, msg, hdr, errString);
	}
}
