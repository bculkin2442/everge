package bjc.everge;

import java.util.ArrayList;
import java.util.List;

public class ReplParseException extends RuntimeException {
	public List<ReplError> errs;

	public ReplParseException(String msg) {
		this(msg, new ArrayList<>());
	}

	public ReplParseException(String msg, List<ReplError> errs) {
		super(msg);

		this.errs = errs;
	}

	@Override
	public String toString() {
		String errString;
		if (errs.size() == 0) errString = "An error";
		else                  errString = "Errors";

		return String.format("%s occured parsing replacement pairs: %s\n%s",
				errString, getMessage(), errs);
	}

	public String toPrintString() {
		StringBuilder errString = new StringBuilder("[ERROR] ");

		if (errs.size() == 0) {
			errString.append("No specific errors");
		} else if (errs.size() == 1) {
			errString.append("An error");
		} else {
			errString.append(errs.size());
			errString.append(" errors");
		}

		errString.append(" occured parsing replacement pairs:");
		if (!getMessage().equals("")) {
			errString.append(" ");
			errString.append(getMessage());
		}

		if (errs.size() > 0) {
			errString.append("\n\t");

			for (ReplError err : errs) {
				errString.append(err.toPrintString("\t"));
				errString.append("\n\t");
			}
		}

		return errString.toString().trim();
	}
}
