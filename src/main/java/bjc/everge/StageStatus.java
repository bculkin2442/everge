package bjc.everge;

/**
 * Possible statuses of pairs with respect to exporting.
 * @author Ben Culkin
 */
public enum StageStatus {
	/**
	 * Only use for staging pairs; don't export.
	 */
	INTERNAL,
	/**
	 * Don't use for staging pairs; do export.
	 */
	EXTERNAL,
	/**
	 * Use for staging pairs; do export.
	 */
	BOTH;
}

