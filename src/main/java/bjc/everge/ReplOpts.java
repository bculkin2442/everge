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
	public boolean equals(Object o) {
		if (o == null) return false;

		if (!getClass().equals(o.getClass())) return false;

		ReplOpts ro = (ReplOpts)o;

		if (isPerf != ro.isPerf) return false;

		if (isDebug != ro.isDebug) return false;
		if (isTrace != ro.isTrace) return false;

		if (defPrior != ro.defPrior) return false;
		if (defStage != ro.defStage) return false;
		if (defMulti != ro.defMulti) return false;

		if (defStatus != ro.defStatus) return false;

		return true;
	}
}
