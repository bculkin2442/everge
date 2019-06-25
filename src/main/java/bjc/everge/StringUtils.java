package bjc.everge;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import java.util.regex.Pattern;

/**
 * Utility methods for strings.
 *
 * @author Ben Culkin.
 */
public class StringUtils {
	public static boolean isDebug = false;

	/**
	 * Split a string on every occurance of a string not preceeded by an escape.
	 *
	 * @param escape
	 * 		The escape that stops splitting.
	 * @param splat
	 * 		The string to split on.
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
		int idx = wrk.indexOf(splat);

		if (isDebug) {
			System.err.printf("[DEBUG] 'hard' escapeSplit: (%s) (%s) (%s) init: %d\n",
					escape, splat, inp, idx);
		}

		while (idx != -1) {
			boolean hasEscape = wrk.regionMatches(idx - 1, escape, 0, escape.length());

			while (idx != -1 && hasEscape) {
				int oidx = wrk.indexOf(splat, idx + 1);

				if (isDebug) {
					System.err.printf("[TRACE] idx: %d, oidx: %d\n", idx, oidx);
				}

				idx = oidx;

				hasEscape = wrk.regionMatches(idx - 1, escape, 0, escape.length());
			}
		
			if (idx == -1) {
				break;
			}

			if (isDebug) {
				System.err.printf("[TRACE] sliced string into (%s) and (%s) at %d\n",
						wrk.substring(0, idx), wrk.substring(idx), idx);
			}

			String tmp = wrk.substring(0, idx);
			ret.add(tmp);
			if (!tmp.equals("") && wrk.endsWith(tmp)) {
				wrk = "";
			} else {
				wrk = wrk.substring(idx + splat.length());
			}

			idx = wrk.indexOf(splat);
		}

		if (isDebug) {
			System.err.printf("\t[TRACE] Remnant is (%s) for string (%s)\n", wrk, inp);
		}

		if (!wrk.equals("")) ret.add(wrk);

		return ret.toArray(new String[0]);
	}
}
