package bjc.everge;

import java.util.function.*;

/**
 * String pairs for replacements.
 *
 * @author Ben Culkin
 */
public class ReplPair implements Comparable<ReplPair>, UnaryOperator<String> {
	// Line number we read this pair from
	int lineNumber;

	// Stage this pair is in
	int stage;

	// Status of this pair with regards to doing staging stuff
	StageStatus stat = StageStatus.BOTH;

	/**
	 * The priority for this replacement.
	 */
	public int priority;

	/**
	 * The name of this replacement.
	 *
	 * Defaults to the 'find' string.
	 */
	public String name;

	/**
	 * The guard for this replacement.
	 *
	 * The guard of the replacement is a regex that has to match before the pair
	 * will be considered. Defaults to being blank.
	 */
	public String guard;

	/**
	 * The string to look for.
	 */
	public String find;

	/**
	 * The string to replace it with.
	 */
	public String replace;

	/**
	 * Create a new blank replacement pair.
	 */
	public ReplPair() {
		this("", "", 1, null);
	}

	/**
	 * Create a new replacement pair with a priority of 1.
	 *
	 * @param f
	 *          The string to find.
	 * @param r
	 *          The string to replace.
	 */
	public ReplPair(String f, String r) {
		this(f, r, 1);
	}

	/**
	 * Create a new named replacement pair with a priority of 1.
	 *
	 * @param f
	 *          The string to find.
	 * @param r
	 *          The string to replace.
	 * @param n
	 *          The name of the replacement pair.
	 */
	public ReplPair(String f, String r, String n) {
		this(f, r, 1, n);
	}

	/**
	 * Create a new replacement pair with a set priority.
	 *
	 * @param f
	 *          The string to find.
	 * @param r
	 *          The string to replace.
	 * @param p
	 *          The priority for the replacement.
	 */
	public ReplPair(String f, String r, int p) {
		this(f, r, p, f);
	}

	/**
	 * Create a new replacement pair with a set priority and name.
	 *
	 * @param f
	 *          The string to find.
	 * @param r
	 *          The string to replace.
	 * @param n
	 *          The name of the replacement pair.
	 * @param p
	 *          The priority for the replacement.
	 */
	public ReplPair(String f, String r, int p, String n) {
		find = f;
		replace = r;

		name = n;

		priority = p;
	}

	@Override
	public String apply(String inp) {
		if (guard != null) {
			if (!inp.matches(guard))
				return inp;
		}

		// FIXME :EndingSlash Ben Culkin 5/20/20
		// In the event that replace ends with a \, that throws a confusing exception
		String res = inp.replaceAll(find, replace);

		return res;
	}

	@Override
	public String toString() {
		String nameStr = "";

		if (!find.equals(name))
			nameStr = String.format("(%s)", name);

		return String.format("%ss/(%s)/(%s)/p(%d)", nameStr, find, replace, priority);
	}

	@Override
	public int compareTo(ReplPair rp) {
		if (this.priority == rp.priority)
			return this.lineNumber - rp.lineNumber;

		return rp.priority - this.priority;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((find == null) ? 0 : find.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + priority;
		result = prime * result + ((replace == null) ? 0 : replace.hashCode());
		result = prime * result + stage;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReplPair other = (ReplPair) obj;
		if (find == null) {
			if (other.find != null)
				return false;
		} else if (!find.equals(other.find))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (priority != other.priority)
			return false;
		if (replace == null) {
			if (other.replace != null)
				return false;
		} else if (!replace.equals(other.replace))
			return false;
		if (stage != other.stage)
			return false;
		return true;
	}
}
