package bjc.everge;

import java.util.*;

import java.util.regex.Pattern;

/**
 * Utility methods for strings.
 *
 * @author Ben Culkin.
 */
public class StringUtils {
	/**
	 * Is the class in debug mode or not?
	 */
	public static boolean isDebug = false;

	/**
	 * Split a string on every occurrence of a string not preceded by an escape.
	 *
	 * @param escape
	 * 		The escape that stops splitting.
	 * @param splat
	 * 		The string to split on. If this starts with the escape sequence, things will work
	 * 		poorly.
	 * @param inp
	 * 		The string to split.
	 * @return The string split as specified above.
	 */
	public static String[] escapeSplit(String escape, String splat, String inp) {
		/*
		 * Special case some stuffs.
		 */

		// No input
		if (inp == null || inp.equals("")) {
			return new String[] {inp};
		}

		// Input does not contain any delimiters
		if (!inp.contains(splat)) {
			return new String[] {inp};
		}

		// No escape, so we can just split normally
		if (escape == null || escape.equals("")) {
			return inp.split(Pattern.quote(splat));
		}

		List<String> ret = new ArrayList<>();

		/*
		 * Set up working variables
		 */

		// Copy of parameters
		String wrk = inp;

		// Index of first occurrence of split string
		int sidx = wrk.indexOf(splat);
		// Index of first occurrence of escaped string
		int eidx = wrk.indexOf(escape);

		// Was the last thing we saw an escape?
		// 	This is used to enable the handling of escaping escapes
		boolean hadEscape = false;

		// As long as there an occurrence of either the split/escape
		while (sidx != -1 || eidx != -1) {
			// If there is an escape before a split
			if (eidx > 0 && eidx < sidx) {
				if (isDebug) System.err.printf("[TRACE] Considering escape\n");

				/*
				 * We potentially have an escaped sequence:
				 * 	- either an escaped split
				 * 	- or an escaped escape
				 */

				// Check for an escaped split
				boolean hasEscapedSplit = wrk.startsWith(splat, eidx + escape.length());
				if (hasEscapedSplit) {
					// Skip over it
					int ofst = eidx + splat.length();

					wrk = sliceStringL(wrk, eidx, escape.length());

					// Recalculate indexes
					sidx = wrk.indexOf(splat,  ofst);
					eidx = wrk.indexOf(escape, ofst);

					if (isDebug) {
						System.err.printf("[TRACE] After esc. split (%s) %d/%d\n",
								wrk, sidx, eidx);
					}

					// No pending escape
					hadEscape = false;
					continue;
				}

				// Check for an escaped escape
				boolean hasEscapedEscape = wrk.startsWith(escape, eidx + escape.length());
				if (hasEscapedEscape) {
					// Skip over it
					int ofst = eidx + escape.length();

					wrk = sliceStringL(wrk, eidx, escape.length());

					// Recalculate indexes
					sidx = wrk.indexOf(splat,  ofst);
					eidx = wrk.indexOf(escape, ofst);

					if (isDebug) {
						System.err.printf("[TRACE] After esc. escape (%s)/(%s) %d/%d\n",
								wrk, wrk.substring(ofst), sidx, eidx);
					}

					// There's a pending escape
					hadEscape = true;
					continue;
				}
			}

			// Calculate whether there is currently an escape
			boolean hasEscape = false;
			{
				boolean tmp = wrk.startsWith(escape, sidx - escape.length());
				// boolean tmp = wrk.regionMatches(lo, escape, 0, escape.length());

				hasEscape = hadEscape ? false : tmp;
			}

			// Handle anything that the pending escape may be applied to
			while (sidx != -1 && hasEscape) {
				int oidx = wrk.indexOf(splat, sidx + escape.length());

				if (oidx == -1) break;

				wrk = sliceStringL(wrk, oidx, escape.length());

				sidx = oidx;

				hasEscape = wrk.startsWith(escape, sidx - escape.length());
			}

			if (sidx == -1) {
				break;
			}

			String tmp = wrk.substring(0, sidx);

			if (isDebug) {
				System.err.printf("[TRACE] Adding (%s) to returned splits; (%s)\n",
					tmp, wrk.substring(sidx));
			}

			ret.add(tmp);
			if (!tmp.equals("") && wrk.endsWith(tmp)) {
				wrk = "";
			} else {
				if (wrk.indexOf(splat, sidx) != -1) {
					wrk = wrk.substring(sidx + splat.length());
				} else {
					wrk = wrk.substring(sidx);
				}
			}

			sidx = wrk.indexOf(splat);
			eidx = wrk.indexOf(escape);

			hadEscape = false;
		}

		if (!wrk.equals("")) ret.add(wrk);

		return ret.toArray(new String[0]);
	}

	/**
	 * Slice a substring out of another string.
	 *
	 * @param strang
	 * 	The string to remove a substring from.
	 * @param lft
	 * 	The left-side of the substring to remove.
	 * @param rft
	 * 	The right-side of the substring to remove.
	 *
	 * @return The string, with the substring removed.
	 */
	public static String sliceString(String strang, int lft, int rft) {
		String leftSide  = strang.substring(0, lft);
		String rightSide = strang.substring(rft);

		return leftSide + rightSide;
	}

	/**
	 * Slice a substring out of another string.
	 *
	 * @param strang
	 * 	The string to remove a substring from.
	 * @param lft
	 * 	The left-side of the substring to remove.
	 * @param len
	 * 	The length of the substring to remove.
	 *
	 * @return The string, with the substring removed.
	 */
	public static String sliceStringL(String strang, int lft, int len) {
		String leftSide  = strang.substring(0, lft);
		String rightSide = strang.substring(lft + len);

		return leftSide + rightSide;
	}
}
