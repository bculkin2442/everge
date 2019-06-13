package bjc.replpair;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import java.util.function.UnaryOperator;

/**
 * String pairs for replacements.
 *
 * @author Ben Culkin
 */
public class ReplPair implements Comparable<ReplPair>, UnaryOperator<String> {
	private int lno;

	/**
	 * The priority for this replacement.
	 */
	public int priority;

	/**
	 * The name of this replacement.
	 *
	 * Defaults to the 'find' string.
	 */
	public String name;
	/**
	 * The string to look for.
	 */
	public String find;

	/**
	 * The string to replace it with.
	 */
	public String replace;

	private StageStatus stat = StageStatus.BOTH;

	/**
	 * Create a new blank replacement pair.
	 */
	public ReplPair() {
		this("", "", 1, null);
	}

	/**
	 * Create a new replacement pair with a priority of 1.
	 *
	 * @param f
	 * 	The string to find.
	 * @param r
	 * 	The string to replace.
	 */
	public ReplPair(String f, String r) {
		this(f, r, 1);
	}

	/**
	 * Create a new named replacement pair with a priority of 1.
	 *
	 * @param f
	 * 	The string to find.
	 * @param r
	 * 	The string to replace.
	 * @param n
	 * 	The name of the replacement pair.
	 */
	public ReplPair(String f, String r, String n) {
		this(f, r, 1, n);
	}

	/**
	 * Create a new replacement pair with a set priority.
	 *
	 * @param f
	 * 	The string to find.
	 * @param r
	 * 	The string to replace.
	 * @param p
	 * 	The priority for the replacement.
	 */
	public ReplPair(String f, String r, int p) {
		this(f, r, p, f);
	}

	/**
	 * Create a new replacement pair with a set priority and name.
	 *
	 * @param f
	 * 	The string to find.
	 * @param r
	 * 	The string to replace.
	 * @param n
	 * 	The name of the replacement pair.
	 * @param p
	 * 	The priority for the replacement.
	 */
	public ReplPair(String f, String r, int p, String n) {
		find    = f;
		replace = r;

		name = n;

		priority = p;
	}

	/**
	 * Read a list of replacement pairs from an input source.
	 *
	 * @param scn
	 * 	The source to read the replacements from.
	 * @return
	 * 	The list of replacements.
	 */
	public static List<ReplPair> readList(Scanner scn) {
		List<ReplPair> lst = new ArrayList<>();

		return readList(lst, scn);
	}

	/**
	 * Read a list of replacement pairs from an input source, adding them to
	 * an existing list.
	 *
	 * @param detals
	 * 	The list to add the replacements to.
	 * @param scn
	 * 	The source to read the replacements from.
	 * @return
	 * 	The list of replacements.
	 */
	public static List<ReplPair> readList(List<ReplPair> detals, Scanner scn) {
		List<ReplError> errList = new ArrayList<>();

		List<ReplPair> rplPar = readList(detals, scn, errList);

		if (errList.size() != 0) {
			String errString;
			if (errList.size() == 0) errString = "An error";
			else                       errString = "Errors";

			throw new IllegalArgumentException(String.format("%s occured parsing replacement pairs:\n%s", errString, errList));
		}

		return rplPar;
	}

	/**
	 * Read a list of replacement pairs from an input source, adding them to
	 * an existing list.
	 *
	 * @param detals
	 * 	The list to add the replacements to.
	 * @param scn
	 * 	The source to read the replacements from.
	 * @param errs
	 * 	The list to stick errors in.
	 * @return
	 * 	The list of replacements.
	 */
	public static List<ReplPair> readList(List<ReplPair> detals, Scanner scn, List<ReplError> errs) {
		return readList(detals, scn, errs, new ReplOpts());
	}

	public static List<ReplPair> readList(List<ReplPair> detals, Scanner scn,
			List<ReplError> errs, ReplOpts ropts) {
		int lno = 0;
		int pno = 0;

		int defPrior = ropts.defPrior;
		int defStage = ropts.defStage;

		boolean defMulti = ropts.defMulti;

		StageStatus defStatus = ropts.defStatus;

		boolean isDebug = ropts.isDebug;

		List<List<ReplPair>> stages = new ArrayList<>();
		stages.add(new ArrayList<ReplPair>());

		// For every line in the source...
		while (scn.hasNextLine()) {
			String name = scn.nextLine().trim();
			lno += 1;

			// If its commented or blank, skip it
			if (name.equals(""))      continue;
			if (name.startsWith("#")) continue;

			// Global control. Process it.
			if (name.startsWith("|//")) {
				name = name.substring(3);

				// Split out each control
				String[] bits = name.split(";");

				for (String bit : bits) {
					String bitHead = bit.toUpperCase();
					String bitBody = bit;

					int idx = bit.indexOf('/');
					if (idx != -1) {
						bitHead = bit.substring(0, idx).toUpperCase();
						bitBody = bit.substring(idx + 1);
					}

					switch (bitHead) {
					case "P":
						try {
							defPrior = Integer.parseInt(bitBody);
						} catch (NumberFormatException nfex) {
							String errMsg = String.format("'%s' is not a valid priority (must be an integer)", bitBody);
							errs.add(new ReplError(lno, pno, errMsg, name));
						}
						break;
					case "S":
						try {
							int tmpStage = Integer.parseInt(bitBody);
							if (tmpStage < 0) {
								String errMsg = String.format("'%s' is not a valid stage (must be a positive integer)", bitBody);
								errs.add(new ReplError(lno, pno, errMsg, name));

								break;
							}
							defStage = tmpStage;
						} catch (NumberFormatException nfex) {
							String errMsg = String.format("'%s' is not a valid stage (must be a positive integer)", bitBody);
							errs.add(new ReplError(lno, pno, errMsg, name));
						}
						break;
					case "MT":
						defMulti = true;
						break;
					case "MF":
						defMulti = false;
						break;
					case "M":
						defMulti = Boolean.parseBoolean(bitBody);
						break;
					case "I":
						defStatus = StageStatus.INTERNAL;
						break;
					case "E":
						defStatus = StageStatus.EXTERNAL;
						break;
					case "B":
						defStatus = StageStatus.BOTH;
						break;
					case "DT":
						isDebug = true;
						break;
					case "DF":
						isDebug = false;
						break;
					case "D":
						isDebug = Boolean.parseBoolean(bitBody);
						break;
					default: 
						errs.add(new ReplError(lno, pno, String.format("Invalid control name '%s'", bitHead), name));
						break;
					}
				}

				continue;
			}

			ReplPair rp = new ReplPair();
			rp.priority = defPrior;
			rp.stat = defStatus;
			rp.lno = lno;
			
			int stage = defStage;

			boolean isMulti = defMulti;

			// Name has attached controls, process them.
			if (name.startsWith("//")) {
				name = name.substring(2);

				int idx = name.indexOf("//");
				if (idx == -1) {
					String msg = "Did not find control terminator (//) in name where it should be";

					errs.add(new ReplError(lno, pno, msg, name));
					continue;
				}

				String contName = name.substring(0, idx);
				String actName  = name.substring(idx + 2);

				// Split out each control
				String[] bits = contName.split(";");

				for (String bit : bits) {
					String bitHead = bit.toUpperCase();
					String bitBody = bit;

					idx = bit.indexOf('/');
					if (idx != -1) {
						bitHead = bit.substring(0, idx).toUpperCase();
						bitBody = bit.substring(idx + 1);
					}
					
					switch (bitHead) {
					case "N":
						rp.name = bitBody;
						break;
					case "P":
						try {
							rp.priority = Integer.parseInt(bitBody);
						} catch (NumberFormatException nfex) {
							String errMsg = String.format("'%s' is not a valid priority (must be an integer)", bitBody);
							errs.add(new ReplError(lno, pno, errMsg, name));
						}
						break;
					case "S":
						try {
							int tmpStage = Integer.parseInt(bitBody);
							if (tmpStage < 0) {
								String errMsg = String.format("'%s' is not a valid stage (must be a positive integer)", bitBody);
								errs.add(new ReplError(lno, pno, errMsg, name));

								break;
							}
							stage = tmpStage;
						} catch (NumberFormatException nfex) {
							String errMsg = String.format("'%s' is not a valid stage (must be a positive integer)", bitBody);
							errs.add(new ReplError(lno, pno, errMsg, name));
						}
						break;
					case "MT":
						isMulti = true;
						break;
					case "MF":
						isMulti = false;
						break;
					case "M":
						isMulti = Boolean.parseBoolean(bitBody);
						break;
					case "I":
						rp.stat = StageStatus.INTERNAL;
						break;
					case "E":
						rp.stat = StageStatus.EXTERNAL;
						break;
					case "B":
						rp.stat = StageStatus.BOTH;
						break;
					default: 
						errs.add(new ReplError(lno, pno, String.format("Unknown control name '%s'", bitHead), name));
						break;
					}
				}
				
				// Multi-line name with a trailer
				if (isMulti) {
					String tmp = actName;

					while (tmp.endsWith("\\")) {
						boolean incNL = tmp.endsWith("|\\");

						if (!scn.hasNextLine()) break;

						tmp = scn.nextLine().trim();

						if (tmp.equals(""))      continue;
						if (tmp.startsWith("#")) continue;

						actName = String.format("%s%s%s", actName, incNL ? "\n" : "", tmp);
					}
				}
				
				name = actName;
			}

			rp.find = name;
			if (rp.name == null) rp.name = name;

			// We started to process the pair, mark it as being
			// started
			pno += 1;
			String body = null;

			// Read in the next uncommented line
			do {
				if (!scn.hasNextLine()) {
					String msg = 
						"Ran out of input looking for replacement body for raw name " + name;
						
					errs.add(new ReplError(lno, pno, msg, null));
					break;
				}

				body = scn.nextLine().trim();
				lno += 1;
			} while (body.startsWith("#"));

			isMulti = defMulti;

			// Body has attached controls, process them.
			if (body.startsWith("//")) {
				body = body.substring(2);

				int idx = body.indexOf("//");
				if (idx == -1) {
					String msg = "Did not find control terminator (//) in body where it should be";

					errs.add(new ReplError(lno, pno, msg, body));
					continue;
				}

				String contBody = body.substring(0, idx);
				String actBody  = body.substring(idx + 2);

				// Split out each control
				String[] bits = actBody.split(";");

				for (String bit : bits) {
					String bitHead = bit.toUpperCase();
					String bitBody = bit;

					idx = bit.indexOf('/');
					if (idx != -1) {
						bitHead = bit.substring(0, idx).toUpperCase();
						bitBody = bit.substring(idx + 1);
					}

					switch (bitHead) {
					case "MT":
						isMulti = true;
						break;
					case "MF":
						isMulti = false;
						break;
					case "M":
						isMulti = Boolean.parseBoolean(bitBody);
						break;
					default: 
						errs.add(new ReplError(lno, pno, String.format("Invalid control name '%s'", bitHead), body));
						break;
					}
				}

				// Multi-line name with a trailer
				if (isMulti) {
					String tmp = actBody;

					while (tmp.endsWith("\\")) {
						boolean incNL = tmp.endsWith("|\\");

						if (!scn.hasNextLine()) break;

						tmp = scn.nextLine().trim();

						if (tmp.startsWith("#")) continue;

						actBody = String.format("%s%s%s", actBody, incNL ? "\n" : "", tmp);
					}
				}
				
				body = actBody;
			}

			rp.replace = body;

			List<ReplPair> stageList = null;
			if (stage == 0 || stages.size() < (stage - 1)) {
				stageList = stages.get(stage);

				if (stageList == null) {
					stageList = new ArrayList<>();

					stages.add(stage, stageList);
				}
			} else {
				for (int i = stages.size(); i <= stage; i++) {
					stages.add(new ArrayList<>());
				}

				stageList = stages.get(stage);
			}

			if (isDebug) {
				System.err.printf("\t[DEBUG] Stage %d: Added %s\n\t\tContents: %s\n",
						stage, rp, stageList);
			}

			stageList.add(rp);
		}

		// Special-case one-stage processing.
		if (stages.size() == 1) {
			if (isDebug) System.err.printf("\t[DEBUG] Executing single-stage bypass\n");

			for (ReplPair rp : stages.iterator().next()) {
				if (rp.stat == StageStatus.INTERNAL) {
					if (isDebug) System.err.printf("\t[DEBUG] Excluding internal RP %s\n", rp);

					continue;
				}

				detals.add(rp);
			}

			detals.sort(null);

			return detals;
		}

		// Handle stages
		List<ReplPair> tmpList = new ArrayList<>();
		tmpList.addAll(detals);

		if (isDebug) System.err.printf("\t[DEBUG] Stages: %s\n", stages);

		int procStg = 0;
		for (List<ReplPair> stageList : stages) {
			procStg += 1;
			List<ReplPair> curStage = new ArrayList<>();

			if (isDebug) System.err.printf("\t[DEBUG] Staging stage %d of %d: %s\n",
					procStg, stageList.size(), stageList);

			for (ReplPair rp : stageList) {
				// Process through every pair in the previous
				// stages
				for (ReplPair curPar : tmpList) {
					String tmp = rp.replace.replaceAll(curPar.find, curPar.replace);

					if (isDebug && !rp.replace.equals(tmp)) {
						System.err.printf("\t[DEBUG] Staged '%s' -> '%s'\t%s\n",
							rp.replace, tmp, curPar);
					}

					rp.replace = tmp;
				}

				// If we're external; add straight to the output
				if (rp.stat == StageStatus.EXTERNAL) {
					if (isDebug) {
						System.err.printf("\t[DEBUG] Skipped external for staging: %s\n",
								rp);
					}

					detals.add(rp);
				} else {
					if (isDebug) {
						System.err.printf("\t[DEBUG] Added to stage %d: %s\n\t\tContents: %s\n",
								procStg, rp, curStage);
					}

					curStage.add(rp);
				}
			}
			
			tmpList.addAll(curStage);
			tmpList.sort(null);
		}

		// Copy over to output, excluding internals
		for (ReplPair rp : tmpList) {
			if (rp.stat == StageStatus.INTERNAL) {
				if (isDebug) System.err.printf("\t[DEBUG] Excluded internal: %s\n", rp);

				continue;
			}

			detals.add(rp);
		}

		detals.sort(null);

		if (isDebug) {
			System.err.printf("\t[DEBUG] Final output: %s\n", detals);
		}

		return detals;
	}

	@Override
	public String apply(String inp) {
		return inp.replaceAll(find, replace);
	}

	@Override
	public String toString() {
		String nameStr = "";

		if (!find.equals(name)) nameStr = String.format("(%s)", name);

		return String.format("%ss/%s/%s/p(%d)", nameStr, find, replace, priority);
	}
	
	@Override
	public int compareTo(ReplPair rp) {
		if (this.priority == rp.priority) return this.lno - rp.lno;

		return rp.priority - this.priority;
	}
}
