package bjc.everge;

import java.io.*;

import java.util.*;

/**
 * A set of ReplPairs, kept together for easy use
 *
 * @author Ben Culkin
 */
public class ReplPairSet {
	// The list of pairs
	private List<ReplPair> pairList;

	/**
	 * Create a new blank set of pairs.
	 */
	public ReplPairSet() {
		pairList = new ArrayList<>();
	}

	/**
	 * Create a new set of pairs using an existing list of pairs.
	 *
	 * Changes to the list of pairs will carry across to the ReplSet, so be careful
	 * about that.
	 *
	 * @param list
	 *            The list of pairs to use.
	 */
	public ReplPairSet(List<ReplPair> list) {
		pairList = list;
	}

	/**
	 * Load a ReplSet from a file.
	 * 
	 * @param fileName
	 *              The file to load the ReplSet from.
	 * @return A ReplSet, loaded from the file.
	 * @throws IOException
	 *                     if something goes badly reading it.
	 */
	public static ReplPairSet fromFile(String fileName) throws IOException {
		ReplPairSet rs = new ReplPairSet();

		try (FileInputStream fis = new FileInputStream(fileName);
				Scanner scn = new Scanner(fis)) {
			ReplPairParser parser = new ReplPairParser();
			
			rs.pairList = parser.readList(scn);
		}

		return rs;
	}

	/**
	 * Adds more pairs to the ReplSet.
	 *
	 * @param pairs
	 *             The pairs to add to the ReplSet.
	 */
	public void addPairs(List<ReplPair> pairs) {
		for (ReplPair par : pairs) pairList.add(par);

		// Resort the pairs into priority order
		pairList.sort(null);
	}

	/**
	 * Adds more pairs to the ReplSet.
	 *
	 * @param pars
	 *             The pairs to add to the ReplSet.
	 */
	public void addPairs(ReplPair... pars) {
		for (ReplPair par : pars) {
			pairList.add(par);
		}

		// Resort the pairs into priority order
		pairList.sort(null);
	}

	/**
	 * Apply the ReplSet to a string.
	 *
	 * @param val
	 *            The string to apply the ReplSet to.
	 *
	 * @return The result of applying the ReplSet.
	 */
	public String apply(String val) {
		String ret = val;

		for (ReplPair par : pairList) {
			System.err.printf("Applying pair '%s' to string '%s' (original was '%s')\n", par, ret, val);
			String tmp = par.apply(ret);

			ret = tmp;
		}

		return ret;
	}
}
