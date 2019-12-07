package bjc.everge;

import java.util.Arrays;

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
		 * @param ars
		 * 	The arguments of the control.
		 */
		public Control(String nam, String... ars) {
			name = nam;
			args = ars;
		}

		/**
		 * Get the count of arguments this control has.
		 *
		 * @return The number of arguments to this control.
		 */
		public int count() {
			return args.length;
		}

		/**
		 * Get an argument from the control.
		 * 
		 * @param i The index of the argument to get.
		 * @return The argument at that position.
		 */
		public String get(int i) {
			if (i < 0) {
				String msg = String.format("Control argument index must be greater than 0 (was %d)", i);

				throw new IndexOutOfBoundsException(msg);
			}

			if (i > args.length) {
				String msg = String.format("Control argument index must be less than %d (was %d)",
						args.length, i);

				throw new IndexOutOfBoundsException(msg);
			}

			return args[i];
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(name);

			if (args != null && args.length > 0) {
				sb.append("/");

				for (String arg : args) {
					sb.append(arg);
					sb.append(";");
				}
			}

			return sb.toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(args);
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) { return true; }
			if (obj == null) { return false; }
			if (getClass() != obj.getClass()) { return false; }

			Control other = (Control) obj;

			if (name == null) {
				if (other.name != null) { return false; }
			} else if (!name.equals(other.name)) { return false; }

			boolean isArged  = args != null && args.length > 0;
			boolean oIsArged = other.args != null && other.args.length > 0;

			if (isArged && !oIsArged) { return false; }
			if (!isArged && oIsArged) { return false; }

			if (isArged && oIsArged) {
				return Arrays.equals(args, other.args);
			}

			return true;
		}

		/**
		 * Convenient static constructor for static imports.
		 *
		 * @param nam
		 * 	The name of the control.
		 * @param ars
		 * 	The arguments to the control.
		 * @return A control with the right parameters.
		 */
		public static Control C(String nam, String... ars) {
			return new Control(nam, ars);
		}
	}
	
	/**
	 * Parameter class for defining how to parse a ControlledString.
	 *
	 * @author Ben Culkin
	 */
	public static class ParseStrings {
		/**
		 * The indicator for separating controls from the regular string.
		 */
		public String contInd;

		/**
		 * The indicator for separating individual controls.
		 */
		public String contSep;

		/**
		 * The indicator for separating arguments to a control.
		 */
		public String contArg;

		/**
		 * The indicator for escaping any of the indicators (including itself)
		 */
		public String contEsc;

		/**
		 * Create a new set of parse strings.
		 *
		 * @param contInd
		 * 	The control indicator.
		 * @param contSep
		 * 	The control separator.
		 * @param contArg
		 * 	The argument separator.
		 * @param contEsc
		 * 	The control escape.
		 */
		public ParseStrings(String contInd, String contSep, String contArg, String contEsc) {
			this.contInd = contInd;
			this.contSep = contSep;
			this.contArg = contArg;
			this.contEsc = contEsc;
		}

		/**
		 * Convenient static constructor.
		 *
		 * @param contInd
		 * 	The control indicator.
		 * @param contSep
		 * 	The control separator.
		 * @param contArg
		 * 	The argument separator.
		 * @param contEsc
		 * 	The control escape.
		 * @return A new set of control strings.
		 */
		public static ParseStrings PS(String contInd, String contSep, String contArg, String contEsc) {
			return new ParseStrings(contInd, contSep, contArg, contEsc);
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

		this.controls = controls;
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
	 * Get the count of controls.
	 *
	 * @return The number of controls for this string.
	 */
	public int count() {
		return controls.length;
	}

	/**
	 * Parse a controlled string from a regular string.
	 *
	 * The controls must be parsed from the beginning of the string.
	 *
	 * @param lne
	 * 	The string to parse from.
	 * @param strangs
	 * 	The object to read the strings from
	 * @return A parsed control string.
	 */
	public static ControlledString parse(String lne, ParseStrings strangs) 
	{
		if (!lne.startsWith(strangs.contInd)) {
			return new ControlledString(lne);
		}

		String[] bits = StringUtils.escapeSplit(strangs.contEsc, strangs.contInd, lne);

		if (bits.length < 2) {
			String msg = "Did not find control terminator (%s) where it should be";
			msg = String.format(msg, strangs.contInd);

			throw new IllegalArgumentException(msg);
		} 

		ControlledString cs = new ControlledString(bits[0]);
		if (bits.length > 2) cs.strang = bits[2];

		bits = StringUtils.escapeSplit(strangs.contEsc, strangs.contSep, bits[1]);

		cs.controls = new Control[bits.length];

		for (int i = 0; i < bits.length; i++) {
			String bit = bits[i];

			String[] bots = StringUtils.escapeSplit(strangs.contEsc, strangs.contArg, bit);

			Control cont = new Control(bots[0]);

			if (cont.name.length() > 1) {
				cont.name = cont.name.toUpperCase();
			}

			if (bots.length > 1) {
				cont.args = new String[bots.length - 1];
				for (int j = 1; j < bots.length; j++) {
					cont.args[j - 1] = bots[j];
				}
			}

			cs.controls[i] = cont;
		}

		return cs;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("//");

		for (Control cont : controls) {
			sb.append(cont);
		}

		sb.append("//");
		sb.append(strang);

		return sb.toString();
	}
}
