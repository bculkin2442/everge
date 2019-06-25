package bjc.everge;

/**
 * Represents a string with a set of control flags attached to it.
 *
 * @author Ben Culkin
 */
public class ControlledString {
	/**
	 * Represents a single control (a key-values pair)
	 *
	 * @author Ben Culkin
	 */
	public static class Control {
		/**
		 * The name of the control.
		 */
		public String name;

		/**
		 * The arguments to the control.
		 */
		public String[] args;

		/**
		 * Create a new blank control.
		 */
		public Control() {

		}

		/**
		 * Create a new argless control.
		 *
		 * @param nam
		 * 	The name of the control.
		 */
		public Control(String nam) {
			name = nam;
		}

		/**
		 * Create a new control.
		 *
		 * @param nam
		 * 	The name of the control.
		 * @param args
		 * 	The arguments of the control.
		 */
		public Control(String nam, String... ars) {
			name = nam;
			args = ars;
		}
	}

	/**
	 * The string the controls apply to.
	 */
	public String strang;

	/**
	 * The controls that apply to the string.
	 */
	public Control[] controls;

	/**
	 * Create a new blank controlled string.
	 */
	public ControlledString() {
		controls = new Control[0];
	}

	/**
	 * Create a new controlled string without any controls.
	 *
	 * @param strung
	 * 	The string to use.
	 */
	public ControlledString(String strung) {
		strang = strung;

		controls = new Control[0];
	}

	/**
	 * Create a new controlled string.
	 *
	 * @param strung
	 * 	The string to use.
	 * @param controls
	 * 	The controls that apply to the string.
	 */
	public ControlledString(String strung, Control... controls) {
		strang = strung;

		controls = controls;
	}

	/**
	 * Check if the string has controls.
	 *
	 * @return Whether or not the string has controls.
	 */
	public boolean hasControls() {
		return controls.length > 0;
	}

	/**
	 * Parse a controlled string from a regular string.
	 *
	 * The controls must be parsed from the beginning of the string, and are indicated by occurances
	 * of contInd that bracket them from the string. The individual controls are delimited by
	 * instances of contSep, with arguments to them being separated by occurances of contArg.
	 *
	 * Each of those separators (which must be regular strings, not regexes or anything) may be
	 * escaped by preceeding them with a copy of contEsc.
	 *
	 * @param lne
	 * 	The string to parse frmo.
	 * @param contInd
	 * 	The indicator for whether or not there are controls.
	 * @param contSep
	 * 	The separator of individual controls.
	 * @param contArg
	 * 	The separator of control arguments.
	 * @param contEsc
	 * 	The escape string for each of the separators/indicators.
	 *
	 * @return A parsed control string.
	 */
	public static ControlledString parse(String lne, String contInd, String contSep,
			String contArg, String contEsc) {
		ControlledString cs = new ControlledString(lne);

		return cs;
	}
}
