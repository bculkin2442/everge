package bjc.everge;

import java.io.PrintStream;

/**
 * Options for processing ReplPairs.
 *
 * @author Ben Culkin.
 */
public class ReplOpts {
	/**
	 * The default priority.
	 */
	public int defPrior = 0;

	/**
	 * The default stage.
	 */
	public int defStage = 0;

	/**
	 * Whether to process multi-line defns.
	 */
	public boolean defMulti = false;

	/**
	 * Default status.
	 */
	public StageStatus defStatus = StageStatus.BOTH;

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

	public PrintStream outStream = System.out;
	public PrintStream errStream = System.err;
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (defMulti ? 1231 : 1237);
		result = prime * result + defPrior;
		result = prime * result + defStage;
		result = prime * result + ((defStatus == null) ? 0 : defStatus.hashCode());
		result = prime * result + (isDebug ? 1231 : 1237);
		result = prime * result + (isPerf ? 1231 : 1237);
		result = prime * result + (isTrace ? 1231 : 1237);
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ReplOpts other = (ReplOpts) obj;
		if (defMulti != other.defMulti) return false;
		if (defPrior != other.defPrior) return false;
		if (defStage != other.defStage) return false;
		if (defStatus != other.defStatus) return false;
		if (isDebug != other.isDebug) return false;
		if (isPerf != other.isPerf) return false;
		if (isTrace != other.isTrace) return false;
		return true;
	}
}
