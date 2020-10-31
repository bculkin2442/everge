package bjc.everge;

import java.io.PrintStream;

/**
 * Options for processing ReplPairs.
 *
 * @author Ben Culkin.
 */
public class ReplPairOptions {
	/** The default priority. */
	public int defaultPriority = 0;

	/** The default stage. */
	public int defaultStage = 0;

	/**
	 * Whether to process multi-line defns.
	 */
	public boolean defaultMulti = false;

	/**
	 * Default status.
	 */
	public StageStatus defaultStatus = StageStatus.BOTH;

	/**
	 * Enable debug info.
	 */
	public boolean isDebug = true;

	/**
	 * Enable trace info.
	 */
	public boolean isTrace = false;

	/**
	 * Enable performance info.
	 */
	public boolean isPerf = false;

	/**
	 * The stream to print normal output on.
	 */
	public PrintStream outStream = System.out;

	/**
	 * The stream to print error output on.
	 */
	public PrintStream errStream = System.err;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (defaultMulti ? 1231 : 1237);
		result = prime * result + defaultPriority;
		result = prime * result + defaultStage;
		result = prime * result + ((defaultStatus == null) ? 0 : defaultStatus.hashCode());
		result = prime * result + (isDebug ? 1231 : 1237);
		result = prime * result + (isPerf ? 1231 : 1237);
		result = prime * result + (isTrace ? 1231 : 1237);
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
		ReplPairOptions other = (ReplPairOptions) obj;
		if (defaultMulti != other.defaultMulti)
			return false;
		if (defaultPriority != other.defaultPriority)
			return false;
		if (defaultStage != other.defaultStage)
			return false;
		if (defaultStatus != other.defaultStatus)
			return false;
		if (isDebug != other.isDebug)
			return false;
		if (isPerf != other.isPerf)
			return false;
		if (isTrace != other.isTrace)
			return false;
		return true;
	}
}
