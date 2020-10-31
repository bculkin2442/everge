package bjc.everge;

import java.util.Arrays;

/* @FixMe Ben Culkin Oct. 31, 2020 - :LeadingControl
 * 
 * At the moment, this only parses a single control that is at the start of the
 * string. Should this be improved?
 */
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
		 *            The name of the control.
		 */
		public Control(String nam) {
			this();
			
			name = nam;
		}

		/**
		 * Create a new control.
		 *
		 * @param nam
		 *            The name of the control.
		 * @param ars
		 *            The arguments of the control.
		 */
		public Control(String nam, String... ars) {
			this(nam);
			
			args = ars;
		}

		/**
		 * Get the count of arguments this control has.
		 *
		 * @return The number of arguments to this control.
		 */
		public int count() {
			if (args == null) return 0;
			
			return args.length;
		}

		/**
		 * Get an argument from the control.
		 *
		 * @param i
		 *          The index of the argument to get.
		 * @return The argument at that position.
		 */
		public String get(int i) {
			// @Cleanup: I'm pretty sure Java will auto-throw these, so we should
			// remove this stuff. --bculkin, Oct. 31, 2020
			if (i < 0) {
				String msg = String.format(
						"Control argument index must be greater than 0 (was %d)", i);

				throw new IndexOutOfBoundsException(msg);
			}

			if (i > args.length) {
				String msg = String.format(
						"Control argument index must be less than %d (was %d)",
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
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}

			Control other = (Control) obj;

			if (name == null) {
				if (other.name != null) {
					return false;
				}
			} else if (!name.equals(other.name)) {
				return false;
			}

			boolean isArged = args != null && args.length > 0;
			boolean oIsArged = other.args != null && other.args.length > 0;

			if (isArged && !oIsArged) {
				return false;
			}
			if (!isArged && oIsArged) {
				return false;
			}

			if (isArged && oIsArged) {
				return Arrays.equals(args, other.args);
			}

			return true;
		}

		/**
		 * Convenient static constructor for static imports.
		 *
		 * @param nam
		 *            The name of the control.
		 * @param ars
		 *            The arguments to the control.
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
	public static class ControlledStringParseOptions {
		/**
		 * The indicator for separating controls from the regular string.
		 */
		public String controlIndicator;

		/**
		 * The indicator for separating individual controls.
		 */
		public String controlSeparator;

		/**
		 * The indicator for separating arguments to a control.
		 */
		public String controlArgumentSeparator;

		/**
		 * The indicator for escaping any of the indicators (including itself)
		 */
		public String controlEscape;

		/**
		 * Create a new set of parse strings.
		 *
		 * @param controlIndicator
		 *                The control indicator.
		 * @param controlSeparator
		 *                The control separator.
		 * @param controlArgumentSeparator
		 *                The argument separator.
		 * @param controlEscape
		 *                The control escape.
		 */
		public ControlledStringParseOptions(String controlIndicator,
				String controlSeparator, String controlArgumentSeparator,
				String controlEscape) {
			this.controlIndicator = controlIndicator;
			this.controlSeparator = controlSeparator;
			this.controlArgumentSeparator = controlArgumentSeparator;
			this.controlEscape = controlEscape;
		}

		/**
		 * Convenient static constructor.
		 *
		 * @param controlIndicator
		 *                The control indicator.
		 * @param controlSeparator
		 *                The control separator.
		 * @param controlArgumentSeparator
		 *                The argument separator.
		 * @param controlEscape
		 *                The control escape.
		 * @return A new set of control strings.
		 */
		public static ControlledStringParseOptions CSPS(String controlIndicator,
				String controlSeparator, String controlArgumentSeparator,
				String controlEscape) {
			return new ControlledStringParseOptions(controlIndicator, controlSeparator, controlArgumentSeparator, controlEscape);
		}
	}

	/**
	 * The string the controls apply to.
	 */
	public String body;

	/**
	 * The controls that apply to the string.
	 */
	// @NOTE Why is this an array? Would it make more sense for it to be a List
	// of some sort? --bculkin, Oct 31, 2020
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
	 *               The string to use.
	 */
	public ControlledString(String strung) {
		this();
		
		body = strung;
	}

	/**
	 * Create a new controlled string.
	 *
	 * @param strung
	 *                 The string to use.
	 * @param controls
	 *                 The controls that apply to the string.
	 */
	public ControlledString(String strung, Control... controls) {
		this(strung);

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
	 *                The string to parse from.
	 * @param strangs
	 *                The object to read the strings from
	 * @return A parsed control string.
	 */
	public static ControlledString parse(String lne, ControlledStringParseOptions strangs) {
		if (!lne.startsWith(strangs.controlIndicator)) return new ControlledString(lne);

		// Split off initial control
		String[] controlIntervals = StringUtils.escapeSplit(strangs.controlEscape, strangs.controlIndicator, lne);

		if (controlIntervals.length < 2) {
			String msg = "Did not find control terminator (%s) where it should be";
			msg = String.format(msg, strangs.controlIndicator);

			throw new IllegalArgumentException(msg);
		}

		ControlledString controlString = new ControlledString(controlIntervals[0]);
		/* :LeadingControl
		 * ... Is this even correct? It would seem that we are discarding any
		 * text that came before the control.
		 * 
		 * Ideally, what we would want to do is concatenate any non-control text,
		 * and then process each control interval by itself.
		 */
		if (controlIntervals.length > 2) controlString.body = controlIntervals[2];

		// Split the individual controls from the string
		String[] unparsedControls = StringUtils.escapeSplit(strangs.controlEscape,
				strangs.controlSeparator, controlIntervals[1]);
		controlString.controls = new Control[unparsedControls.length];

		for (int i = 0; i < unparsedControls.length; i++) {
			String controlText = unparsedControls[i];

			// Get the control arguments
			String[] controlArguments
					= StringUtils.escapeSplit(strangs.controlEscape, strangs.controlArgumentSeparator, controlText);

			Control control = new Control(controlArguments[0]);

			if (control.name.length() > 1) {
				// Only single-character controls can be lower-case
				control.name = control.name.toUpperCase();
			}

			if (controlArguments.length > 1) {
				control.args = new String[controlArguments.length - 1];
				for (int j = 1; j < controlArguments.length; j++) {
					control.args[j - 1] = controlArguments[j];
				}
			}

			controlString.controls[i] = control;
		}

		return controlString;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("//");

		for (Control control : controls) sb.append(control);

		sb.append("//");
		sb.append(body);

		return sb.toString();
	}
}
