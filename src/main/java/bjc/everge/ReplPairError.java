package bjc.everge;

import bjc.data.IntHolder;

/**
 * Represents an error encountered parsing ReplPairs
 *
 * @author Ben Culkin
 */
public class ReplPairError {
	/**
	 * The line the error occurred on.
	 */
	public int lineNumber;
	/**
	 * The number of pairs we have processed so far.
	 */
	public int numPairsSoFar;

	/**
	 * The text of the line we errored on.
	 */
	public String lineText;
	/**
	 * The message of the error.
	 */
	public String errorMessage;

	/**
	 * Create a new ReplPair parse error.
	 *
	 * @param lineNo
	 *               The line the error occured on.
	 * @param numPairsSoFar
	 *               The number of pairs processed up to this point.
	 * @param errorMessage
	 *               The message detailing the error.
	 * @param lineText
	 *               The text that caused the error.
	 */
	public ReplPairError(IntHolder lineNo, IntHolder numPairsSoFar,
			String errorMessage, String lineText) {
		this(lineNo.get(), numPairsSoFar.get(), errorMessage, lineText);
	}

	/**
	 * Create a new ReplPair parse error.
	 *
	 * @param lineNo
	 *               The line the error occured on.
	 * @param numPairsSoFar
	 *               The number of pairs processed up to this point.
	 * @param errorMessage
	 *               The message detailing the error.
	 * @param lineText
	 *               The text that caused the error.
	 */
	public ReplPairError(int lineNo, int numPairsSoFar, String errorMessage,
			String lineText) {
		this.lineNumber = lineNo;
		this.numPairsSoFar = numPairsSoFar;

		this.lineText = lineText;
		this.errorMessage = errorMessage;
	}

	@Override
	public String toString() {
		String errString;
		if (lineText == null)         errString = "No associated line";
		else if (lineText.equals("")) errString = "Text of line was empty";
		else                          errString = "Text of line was: " + lineText;

		return String.format("line %d, pair %d:%s\n\t%s",
				lineNumber, numPairsSoFar, errorMessage, errString);
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
		
		if (lineText == null)         errString = "No associated line";
		else if (lineText.equals("")) errString = "Text of line was empty";
		else                          errString = "Text of line was: " + lineText;

		return String.format("[ERROR] line %d, pair %d: %s\n%s\tContext: %s",
				lineNumber, numPairsSoFar, errorMessage, hdr, errString);
	}
}
