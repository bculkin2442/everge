package bjc.everge;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods for strings.
 *
 * @author Ben Culkin.
 */
public class StringUtils {
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
		if (escape == null || escape.equals("")) {
			return inp.split(splat);
		}

		List<String> ret = new ArrayList<>();

		String wrk = inp;
		int idx = wrk.indexOf(splat);

		// System.err.printf("[DEBUG] 'hard' escapeSplit: (%s) (%s) (%s) init: %d\n",
		// 		escape, splat, inp, idx);

		while (idx != -1) {
			while (idx != -1 && wrk.regionMatches(idx - 1, escape, 0, escape.length())) {
				int oidx = wrk.indexOf(splat, idx + 1);
				// System.err.printf("[TRACE] idx: %d, oidx: %d\n", idx, oidx);
				idx = oidx;
			}
		
			if (idx == -1) {
				break;
			}

			// System.err.printf("[TRACE] sliced string into (%s) and (%s) at %d\n",
			// 		wrk.substring(0, idx), wrk.substring(idx), idx);
			String tmp = wrk.substring(0, idx);
			ret.add(tmp);
			if (wrk.endsWith(tmp)) {
				wrk = "";
			} else {
				wrk = wrk.substring(idx + splat.length());
			}

			idx = wrk.indexOf(splat);
		}

		if (!wrk.equals("")) ret.add(wrk);

		return ret.toArray(new String[0]);
	}
}
