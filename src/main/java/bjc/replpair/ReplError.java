package bjc.replpair;

/**
 * Represents an error encountered parsing ReplPairs
 *
 * @author Ben Culkin
 */
public class ReplError {
	/**
	 * The line the error occured on.
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
	 * 	The line the error occured on.
	 * @param nPairs
	 * 	The number of pairs processed up to this point.
	 * @param msg
	 * 	The message detailing the error.
	 * @param txt
	 * 	The text that caused the error.
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
		if      (txt == null)    errString = "No associated line";
		else if (txt.equals("")) errString = "Text of line was empty";
		else                     errString = "Text of line was: " + txt;

		return String.format("line %d, pair %d:%s\n\t%s", line, numPairs, msg, errString);
	}
}
