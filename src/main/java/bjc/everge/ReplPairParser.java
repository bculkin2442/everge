package bjc.everge;

import java.util.*;
import java.util.regex.*;

import bjc.data.*;
import bjc.everge.ControlledString.*;

/**
 * Parses instances of ReplPair.
 * 
 * @author Ben Culkin
 *
 */
public class ReplPairParser {
	/**
	 * Read a list of replacement pairs from an input source.
	 *
	 * @param scn
	 *            The source to read the replacements from.
	 * @return The list of replacements.
	 */
	public List<ReplPair> readList(Scanner scn) {
		List<ReplPair> lst = new ArrayList<>();
	
		return readList(lst, scn);
	}

	/**
	 * Read a list of replacement pairs from an input source, adding them to an
	 * existing list.
	 *
	 * @param detals
	 *               The list to add the replacements to.
	 * @param scn
	 *               The source to read the replacements from.
	 * @return The list of replacements.
	 */
	public List<ReplPair> readList(List<ReplPair> detals, Scanner scn) {
		List<ReplPairError> errList = new ArrayList<>();
	
		List<ReplPair> rplPar = readList(detals, scn, errList);
	
		if (errList.size() != 0) {
			throw new BadReplParse("", errList);
		}
	
		return rplPar;
	}

	/**
	 * Read a list of replacement pairs from an input source, adding them to an
	 * existing list.
	 *
	 * @param detals
	 *               The list to add the replacements to.
	 * @param scn
	 *               The source to read the replacements from.
	 * @param errs
	 *               The list to stick errors in.
	 * @return The list of replacements.
	 */
	public List<ReplPair> readList(List<ReplPair> detals, Scanner scn,
			List<ReplPairError> errs) {
		return readList(detals, scn, errs, new ReplPairOptions());
	}

	/**
	 * Read a list of replacement pairs from an input source, adding them to an
	 * existing list.
	 *
	 * @param detals
	 *               The list to add the replacements to.
	 * @param scn
	 *               The source to read the replacements from.
	 * @param errs
	 *               The list to stick errors in.
	 * @param ropts
	 *               The options to use when reading the pairs.
	 * @return The list of replacements.
	 */
	public List<ReplPair> readList(List<ReplPair> detals, Scanner scn,
			List<ReplPairError> errs, ReplPairOptions ropts) {
		IntHolder lno = new IntHolder();
		IntHolder pno = new IntHolder();
	
		List<List<ReplPair>> stages = new ArrayList<>();
		stages.add(new ArrayList<ReplPair>());
	
		// For every line in the source...
		while (scn.hasNextLine()) {
			String name = scn.nextLine().trim();
			lno.incr();
	
			// If its commented or blank, skip it
			if (name.equals(""))
				continue;
			if (name.startsWith("#"))
				continue;
	
			// Global control. Process it.
			if (name.startsWith("|//")) {
				readGlobal(name, errs, ropts, lno, pno);
	
				continue;
			}
	
			ReplPair rp = new ReplPair();
	
			rp.priority = ropts.defaultPriority;
			rp.stat = ropts.defaultStatus;
			rp.lineNumber = lno.get();
			rp.stage = ropts.defaultStage;
	
			boolean isMulti = ropts.defaultMulti;
	
			{
				String tmpName = readName(name, scn, errs, rp, ropts, lno, pno);
				if (tmpName == null)
					continue;
				name = tmpName;
			}
	
			rp.find = name;
			if (rp.name == null)
				rp.name = name;
	
			// We started to process the pair, mark it as being
			// started
			pno.incr();
			String body = null;
	
			// Read in the next uncommented line
			do {
				if (!scn.hasNextLine())
					break;
	
				body = scn.nextLine().trim();
				lno.incr();
			} while (body.startsWith("#"));
	
			if (body == null) {
				String msg = String.format(
						"Ran out of input looking for replacement body for raw name '%s'",
						name);
	
				errs.add(new ReplPairError(lno, pno, msg, null));
				break;
			}
	
			isMulti = ropts.defaultMulti;
	
			ControlledString cs = getControls(body, errs, lno, pno, "body");
			// Body has attached controls, process them.
			if (cs.hasControls()) {
				for (Control cont : cs.controls) {
					switch (cont.name) {
					case "MULTITRUE":
					case "MULTIT":
					case "MT":
						isMulti = true;
						break;
					case "MULTIFALSE":
					case "MULTIF":
					case "MF":
						isMulti = false;
						break;
					case "MULTI":
					case "M":
						if (cont.count() != 1) {
							String errMsg = String.format(
									"Expected one multi flag (got %d)", cont.count());
							errs.add(new ReplPairError(lno, pno, errMsg, body));
						} else {
							isMulti = Boolean.parseBoolean(cont.get(0));
						}
						break;
					default: {
						String errMsg
								= String.format("Invalid control name '%s'", cont.name);
						errs.add(new ReplPairError(lno, pno, errMsg, body));
					}
						break;
					}
				}
	
				body = cs.body;
			}
	
			if (isMulti) {
				String tmp = readMultiLine(body, scn, ropts, "body", lno);
				if (tmp == null)
					continue;
				body = tmp;
			}
	
			rp.replace = body;
	
			List<ReplPair> stageList = null;
			if (rp.stage == 0 || stages.size() < (rp.stage - 1)) {
				stageList = stages.get(rp.stage);
	
				if (stageList == null) {
					stageList = new ArrayList<>();
	
					stages.add(rp.stage, stageList);
				}
			} else {
				for (int i = stages.size(); i <= rp.stage; i++) {
					stages.add(new ArrayList<>());
				}
	
				stageList = stages.get(rp.stage);
			}
	
			if (ropts.isTrace) {
				ropts.errStream.printf("\t[DEBUG] Stage %d: Added %s\n\t\tContents: %s\n",
						rp.stage, rp, stageList);
			}
	
			stageList.add(rp);
		}
	
		// Special-case one-stage processing.
		if (stages.size() == 1) {
			if (ropts.isTrace)
				ropts.errStream.printf("\t[DEBUG] Executing single-stage bypass\n");
	
			for (ReplPair rp : stages.iterator().next()) {
				if (rp.stat == StageStatus.INTERNAL) {
					if (ropts.isTrace)
						ropts.errStream.printf("\t[DEBUG] Excluding internal RP %s\n",
								rp);
	
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
	
		if (ropts.isTrace)
			ropts.errStream.printf("\t[DEBUG] Stages: %s\n", stages);
	
		int procStg = 0;
		for (List<ReplPair> stageList : stages) {
			procStg += 1;
			List<ReplPair> curStage = new ArrayList<>();
	
			if (ropts.isTrace)
				ropts.errStream.printf("\t[DEBUG] Staging stage %d of %d: %s\n", procStg,
						stageList.size(), stageList);
	
			for (ReplPair rp : stageList) {
				// Process through every pair in the previous
				// stages
				for (ReplPair curPar : tmpList) {
					String tmp = rp.replace.replaceAll(curPar.find, curPar.replace);
	
					if (ropts.isTrace && !rp.replace.equals(tmp)) {
						ropts.errStream.printf("\t[DEBUG] Staged '%s' -> '%s'\t%s\n",
								rp.replace, tmp, curPar);
					}
	
					rp.replace = tmp;
				}
	
				// If we're external; add straight to the output
				if (rp.stat == StageStatus.EXTERNAL) {
					if (ropts.isTrace) {
						ropts.errStream.printf(
								"\t[DEBUG] Skipped external for staging: %s\n", rp);
					}
	
					detals.add(rp);
				} else {
					if (ropts.isTrace) {
						ropts.errStream.printf(
								"\t[DEBUG] Added to stage %d: %s\n\t\tContents: %s\n",
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
				if (ropts.isTrace)
					ropts.errStream.printf("\t[DEBUG] Excluded internal: %s\n", rp);
	
				continue;
			}
	
			detals.add(rp);
		}
	
		detals.sort(null);
	
		if (ropts.isTrace) {
			ropts.errStream.printf("\t[DEBUG] Final output: %s\n", detals);
		}
	
		return detals;
	}

	private String readMultiLine(String lead, Scanner src, ReplPairOptions ropts,
			String typ, IntHolder lno) {
		String tmp = lead;
	
		if (ropts.isTrace && tmp.endsWith("\\"))
			ropts.errStream.printf("\t[TRACE] Starting multi-line parse for %s '%s'\n",
					typ, tmp);
	
		boolean didMulti = tmp.endsWith("\\");
		while (tmp.endsWith("\\")) {
			boolean incNL = tmp.endsWith("|\\");
	
			if (!src.hasNextLine())
				break;
	
			String nxt = src.nextLine().trim();
			lno.incr();
	
			if (nxt.startsWith("#"))
				continue;
	
			String nlStr = incNL ? "\n" : "";
	
			if (tmp.endsWith("\\")) {
				if (incNL) {
					tmp = tmp.substring(0, tmp.length() - 2);
				} else {
					tmp = tmp.substring(0, tmp.length() - 1);
				}
			}
	
			tmp = String.format("%s%s%s", tmp, nlStr, nxt);
		}
	
		if (ropts.isTrace && didMulti)
			ropts.errStream.printf("\t[TRACE] Finished multi-line parse for %s:\n%s\n.\n",
					typ, tmp);
	
		return tmp;
	}

	private String readName(String nam, Scanner scn, List<ReplPairError> errs,
			ReplPair rp, ReplPairOptions ropts, IntHolder lno, IntHolder pno) {
		ControlledString cs = getControls(nam, errs, lno, pno, "name");
	
		boolean isMulti = ropts.defaultMulti;
	
		String name = cs.body;
	
		if (cs.hasControls()) {
			for (Control cont : cs.controls) {
				switch (cont.name) {
				case "NAME":
				case "N":
					if (cont.count() != 1) {
						String errMsg = String.format(
								"One name argument was expected (got %d)", cont.count());
	
						errs.add(new ReplPairError(lno, pno, errMsg, nam));
					} else {
						rp.name = cont.get(0);
					}
					break;
				case "GUARD":
				case "G":
					if (cont.count() != 1) {
						String errMsg = String.format(
								"One guard argument was expected (got %d)", cont.count());
	
						errs.add(new ReplPairError(lno, pno, errMsg, nam));
					} else {
						String pat = cont.get(0);
	
						try {
							Pattern.compile(pat);
						} catch (PatternSyntaxException psex) {
							String errMsg = String.format(
									"Guard argument '%s' is not a valid regex (%s)", pat,
									psex.getMessage());
	
							errs.add(new ReplPairError(lno, pno, errMsg, nam));
						}
	
						rp.guard = cont.get(0);
					}
					break;
				case "PRIORITY":
				case "PRIOR":
				case "P":
					try {
						if (cont.count() != 1) {
							String errMsg = String.format(
									"One priority argument was expected (got %d",
									cont.count());
	
							errs.add(new ReplPairError(lno, pno, errMsg, nam));
						} else {
							rp.priority = Integer.parseInt(cont.get(0));
						}
					} catch (NumberFormatException nfex) {
						String errMsg = String.format(
								"'%s' is not a valid priority (must be an integer)",
								cont.get(0));
	
						errs.add(new ReplPairError(lno, pno, errMsg, nam));
					}
					break;
				case "STAGE":
				case "S":
					try {
						if (cont.count() != 1) {
							String errMsg = String.format(
									"One stage argument was expected (got %d",
									cont.count());
	
							errs.add(new ReplPairError(lno, pno, errMsg, nam));
						} else {
							int tmpStage = Integer.parseInt(cont.get(0));
							if (tmpStage < 0) {
								String errMsg = String.format(
										"'%s' is not a valid stage (must be a positive integer)",
										cont.get(0));
								errs.add(new ReplPairError(lno, pno, errMsg, nam));
	
								break;
							}
							rp.stage = tmpStage;
						}
					} catch (NumberFormatException nfex) {
						String errMsg = String.format(
								"'%s' is not a valid stage (must be a positive integer)",
								cont.get(0));
	
						errs.add(new ReplPairError(lno, pno, errMsg, nam));
					}
					break;
				case "MULTITRUE":
				case "MULTIT":
				case "MT":
					isMulti = true;
					break;
				case "MULTIFALSE":
				case "MULTIF":
				case "MF":
					isMulti = false;
					break;
				case "MULTI":
				case "M":
					if (cont.count() != 1) {
						String errMsg = String.format(
								"One multi-flag argument was expected (got %d",
								cont.count());
	
						errs.add(new ReplPairError(lno, pno, errMsg, nam));
					} else {
						isMulti = Boolean.parseBoolean(cont.get(0));
					}
					break;
				case "INTERNAL":
				case "INT":
				case "I":
					rp.stat = StageStatus.INTERNAL;
					break;
				case "EXTERNAL":
				case "EXT":
				case "E":
					rp.stat = StageStatus.EXTERNAL;
					break;
				case "BOTH":
				case "B":
					rp.stat = StageStatus.BOTH;
					break;
				default: {
					String errMsg = String.format(
							"Unknown control name '%s' for name '%s'", cont.name, nam);
	
					ReplPairError erd = new ReplPairError(lno, pno, errMsg, nam);
	
					errs.add(erd);
				}
					break;
				}
			}
	
			name = cs.body;
		}
	
		// Multi-line name with a trailer
		if (isMulti) {
			String tmp = readMultiLine(name, scn, ropts, "name", lno);
			if (tmp == null)
				return null;
			name = tmp;
		}
	
		return name;
	}

	private void readGlobal(String nam, List<ReplPairError> errs,
			ReplPairOptions ropts, IntHolder lno, IntHolder pno) {
		ControlledString cs
				= getControls(nam.substring(1), errs, lno, pno, "global");
	
		for (Control cont : cs.controls) {
			switch (cont.name) {
			case "PRIORITY":
			case "PRIOR":
			case "P":
				try {
					if (cont.count() != 1) {
						String errMsg = String.format(
								"Must specify 1 priority (%d specified)", cont.count());
	
						errs.add(new ReplPairError(lno, pno, errMsg, nam));
					} else {
						int tmp = Integer.parseInt(cont.get(0));
						ropts.defaultPriority = tmp;
					}
				} catch (NumberFormatException nfex) {
					String errMsg = String.format(
							"'%s' is not a valid priority (must be an integer)",
							cont.get(0));
	
					errs.add(new ReplPairError(lno, pno, errMsg, nam));
				}
				break;
			case "STAGE":
			case "S":
				try {
					if (cont.count() != 1) {
						String errMsg = String.format(
								"Must specify 1 stage (%d specified)", cont.count());
	
						errs.add(new ReplPairError(lno, pno, errMsg, nam));
					} else {
						int tmpStage = Integer.parseInt(cont.get(0));
	
						if (tmpStage < 0) {
							String errMsg = String.format(
									"'%s' is not a valid stage (must be a positive integer)",
									cont.get(0));
	
							errs.add(new ReplPairError(lno, pno, errMsg, nam));
							break;
						}
	
						ropts.defaultStage = tmpStage;
					}
				} catch (NumberFormatException nfex) {
					String errMsg = String.format(
							"'%s' is not a valid stage (must be a positive integer)",
							cont.get(0));
	
					errs.add(new ReplPairError(lno, pno, errMsg, nam));
				}
				break;
			case "MULTITRUE":
			case "MULTIT":
			case "MT":
				ropts.defaultMulti = true;
				break;
			case "MULTIFALSE":
			case "MULTIF":
			case "MF":
				ropts.defaultMulti = false;
				break;
			case "MULTI":
			case "M":
				if (cont.count() != 1) {
					String errMsg = String.format(
							"Must specify 1 multi-flag (%d specified)", cont.count());
	
					errs.add(new ReplPairError(lno, pno, errMsg, nam));
				} else {
					ropts.defaultMulti = Boolean.parseBoolean(cont.get(0));
				}
				break;
			case "INTERNAL":
			case "INT":
			case "I":
				ropts.defaultStatus = StageStatus.INTERNAL;
				break;
			case "EXTERNAL":
			case "EXT":
			case "E":
				ropts.defaultStatus = StageStatus.EXTERNAL;
				break;
			case "BOTH":
			case "B":
				ropts.defaultStatus = StageStatus.BOTH;
				break;
			case "DEBUGTRUE":
			case "DEBUGT":
			case "DT":
				ropts.isDebug = true;
				break;
			case "DEBUGFALSE":
			case "DEBUGF":
			case "DF":
				ropts.isDebug = false;
				break;
			case "DEBUG":
			case "D":
				if (cont.count() != 1) {
					String errMsg = String.format(
							"Must specify 1 debug flag (%d specified)", cont.count());
	
					errs.add(new ReplPairError(lno, pno, errMsg, nam));
				} else {
					ropts.isDebug = Boolean.parseBoolean(cont.get(0));
				}
				break;
			case "TRACETRUE":
			case "TRACET":
			case "TT":
				ropts.isTrace = true;
				break;
			case "TRACEFALSE":
			case "TRACEF":
			case "TF":
				ropts.isTrace = false;
				break;
			case "TRACE":
			case "T":
				if (cont.count() != 1) {
					String errMsg = String.format(
							"Must specify 1 trace flag (%d specified)", cont.count());
	
					errs.add(new ReplPairError(lno, pno, errMsg, nam));
				} else {
					ropts.isTrace = Boolean.parseBoolean(cont.get(0));
				}
				break;
			case "PERFTRUE":
			case "PERFT":
			case "PRFT":
				ropts.isPerf = true;
				break;
			case "PERFFALSE":
			case "PERFF":
			case "PRFF":
				ropts.isPerf = false;
				break;
			case "PERF":
			case "PRF":
				if (cont.count() != 1) {
					String errMsg = String.format(
							"Must specify 1 perf. flag (%d specified)", cont.count());
	
					errs.add(new ReplPairError(lno, pno, errMsg, nam));
				} else {
					ropts.isPerf = Boolean.parseBoolean(cont.get(0));
				}
				break;
			default: {
				String msg = String.format("Invalid global control name '%s'", cont.name);
				ReplPairError err = new ReplPairError(lno, pno, msg, nam);
				errs.add(err);
			}
				break;
			}
	
			if (ropts.isTrace)
				ropts.errStream.printf("\t[TRACE] Processed global control '%s'\n", cont);
		}
	
		return;
	}

	private ControlledString getControls(String lne, List<ReplPairError> errs,
			IntHolder lno, IntHolder pno, String type) {
		try {
			return ControlledString.parse(lne, new ControlledStringParseOptions("//", ";", "/", "|"));
		} catch (IllegalArgumentException iaex) {
			String msg = "Did not find control terminator (//) in %s where it should be";
			msg = String.format(msg, type);
	
			ReplPairError re = new ReplPairError(lno, pno, msg, lne);
			errs.add(re);
	
			return null;
		}
	}

}
