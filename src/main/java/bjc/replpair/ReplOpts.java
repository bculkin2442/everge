package bjc.replpair;

/**
 * Options for processing ReplPairs.
 *
 * @author Ben Culkin.
 */
public class ReplOpts {
	/**
	 * The default priority.
	 */
	public int defPrior;
	/**
	 * The default stage.
	 */
	public int defStage;

	/**
	 * Whether to process multi-line defns.
	 */
	public boolean defMulti;

	/**
	 * Default status.
	 */
	public StageStatus defStatus;

	/**
	 * Enable debug info.
	 */
	public boolean isDebug;

	/**
	 * Create a default set of options.
	 */
	public ReplOpts() {
		defPrior = 0;
		defStage = 0;

		defMulti = false;

		defStatus = StageStatus.BOTH;

		isDebug = false;
	}

	/**
	 * Create a new set of repl. opts
	 *
	 * @param p
	 * 		The default priority to use
	 * @param s
	 * 		The default stage to use
	 * @param m
	 * 		Whether to process multi-line defns.
	 * @param t
	 * 		The default status.
	 */
	public ReplOpts(int p, int s, boolean m, StageStatus t, boolean d) {
		defPrior = p;
		defStage = s;

		defMulti = m;

		defStatus = t;

		isDebug = d;
	}
}
