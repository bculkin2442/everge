package bjc.everge;

/**
 * Utility class for ints by ref.
 *
 * @author Ben Culkin
 */
public class IntHolder {
	/**
	 * The int value.
	 */
	public int val;

	/**
	 * Create a new int-holder set to 0.
	 */
	public IntHolder() {
		val = 0;
	}

	/**
	 * Create a new int-holder set to a value.
	 *
	 * @param i
	 * 		The value to set the int to.
	 */
	public IntHolder(int i) {
		val = i;
	}

	/**
	 * Increment the value by one, and return it.
	 *
	 * @return The value of the holder.
	 */
	public int incr() {
		return incr(1);
	}

	/**
	 * Increment the value by an amount and return it.
	 *
	 * @param i
	 * 		The amount to increment by.
	 *
	 * @return The value of the holder.
	 */
	public int incr(int i) {
		val += 1;

		return val;
	}

	/**
	 * Get the value.
	 *
	 * @return The value.
	 */
	public int get() {
		return val;
	}

	public void set(int i) {
		val = i;
	}
}
