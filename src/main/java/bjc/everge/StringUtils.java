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
		if (inp == null || inp.equals("")) {
			// No input
			return new String[] {inp};
		}

		if (!inp.contains(splat)) {
			// Input does not contain any delimiters
			return new String[] {inp};
		}

		if (escape == null || escape.equals("")) {
			// No escape, so we can just split normally
			return inp.split(Pattern.quote(splat));
		}

		List<String> ret = new ArrayList<>();

		String wrk = inp;
		int sidx = wrk.indexOf(splat);
		int eidx = wrk.indexOf(escape);

		boolean hadEscape = false;

		while (sidx != -1 || eidx != -1) {
			if (eidx > 0 && eidx < sidx) {
				if (isDebug) System.err.printf("[TRACE] Considering escape\n");

				/*
				 * We potentially have an escaped sequence:
				 * 	- either an escaped split
				 * 	- or an escaped escape
				 */
				// Check for an escaped split
				if (wrk.regionMatches(eidx + escape.length(), splat, 0, splat.length())) {
					// Skip over it
					int ofst = eidx + splat.length();

					// Slice out the escape
					{
						String s1 = wrk.substring(0, eidx);
						String s2 = wrk.substring(eidx + escape.length());

						String s3 = wrk.substring(eidx, eidx + escape.length());

						if (isDebug) {
							System.err.printf("[TRACE] Skip esc. split (%s)/(%s); (%s)\n",
									s1, s2, s3);
						}

						wrk = s1 + s2;
					}

					sidx = wrk.indexOf(splat,  ofst);
					eidx = wrk.indexOf(escape, ofst);

					if (isDebug) {
						System.err.printf("[TRACE] After esc. split (%s) %d/%d\n",
								wrk, sidx, eidx);
					}

					hadEscape = false;
					continue;
				}

				// Check for an escaped escape
				if (wrk.regionMatches(eidx + escape.length(), escape, 0, escape.length())) {
					// Skip over it
					int ofst = eidx + escape.length();

					// Slice out the escape
					{
						String s1 = wrk.substring(0, eidx);
						String s2 = wrk.substring(eidx + escape.length());

						String s3 = wrk.substring(eidx, eidx + escape.length());
						if (isDebug) {
							System.err.printf("[TRACE] Skip esc. escape (%s)/(%s); (%s)\n",
								s1, s2, s3);
						}

						wrk = s1 + s2;
					}

					sidx = wrk.indexOf(splat,  ofst);
					eidx = wrk.indexOf(escape, ofst);

					if (isDebug) {
						System.err.printf("[TRACE] After esc. escape (%s)/(%s) %d/%d\n",
								wrk, wrk.substring(ofst), sidx, eidx);
					}

					hadEscape = true;
					continue;
				}
			}

			boolean hasEscape = false;

			{
				boolean tmp = wrk.regionMatches(sidx - escape.length(), escape, 0, escape.length());

				hasEscape = hadEscape ? false : tmp;
			}

			while (sidx != -1 && hasEscape) {
				int oidx = wrk.indexOf(splat, sidx + escape.length());

				if (oidx == -1) break;

				{
					String s1 = wrk.substring(0, oidx);
					String s2 = wrk.substring(oidx + escape.length());

					wrk = s1 + s2;
				}

				sidx = oidx;

				hasEscape = wrk.regionMatches(sidx - escape.length(), escape, 0, escape.length());
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
}
