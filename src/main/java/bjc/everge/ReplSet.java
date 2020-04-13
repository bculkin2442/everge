package bjc.everge;

import java.io.*;

import java.util.*;

/**
 * A set of ReplPairs, kept together for easy use
 *
 * @author Ben Culkin
 */
public class ReplSet {
	// The list of pairs
	private List<ReplPair> parList;

	/**
	 * Create a new blank set of pairs.
	 */
	public ReplSet() {
		parList = new ArrayList<>();
	}

	/**
	 * Create a new set of pairs using an existing list of pairs.
	 *
	 * Changes to the list of pairs will carry across to the ReplSet, so be careful
	 * about that.
	 *
	 * @param lst
	 *            The list of pairs to use.
	 */
	public ReplSet(List<ReplPair> lst) {
		parList = lst;
	}

	/**
	 * Load a ReplSet from a file.
	 * 
	 * @param fName
	 *              The file to load the ReplSet from.
	 * @return A ReplSet, loaded from the file.
	 * @throws IOException
	 *                     if something goes badly reading it.
	 */
	public static ReplSet fromFile(String fName) throws IOException {
		ReplSet rs = new ReplSet();

		try (FileInputStream fis = new FileInputStream(fName);
				Scanner scn = new Scanner(fis)) {
			rs.parList = ReplPair.readList(scn);
		}

		return rs;
	}

	/**
	 * Adds more pairs to the ReplSet.
	 *
	 * @param pars
	 *             The pairs to add to the ReplSet.
	 */
	public void addPairs(List<ReplPair> pars) {
		for (ReplPair par : pars) {
			parList.add(par);
		}

		// Resort the pairs into priority order
		parList.sort(null);
	}

	/**
	 * Adds more pairs to the ReplSet.
	 *
	 * @param pars
	 *             The pairs to add to the ReplSet.
	 */
	public void addPairs(ReplPair... pars) {
		for (ReplPair par : pars) {
			parList.add(par);
		}

		// Resort the pairs into priority order
		parList.sort(null);
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

		for (ReplPair par : parList) {
			String tmp = par.apply(ret);

			ret = tmp;
		}

		return ret;
	}
}
