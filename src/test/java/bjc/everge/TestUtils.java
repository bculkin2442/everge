package bjc.everge;

import java.io.FileInputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.*;

/**
 * Utility methods for testing.
 *
 * @author Ben Culkin
 */
public class TestUtils {
	/**
	 * Assert that a ReplParseException is thrown with a given message.
	 *
	 * @param msg
	 * 		The message.
	 * @param fle
	 * 		The file to load input from.
	 */
	public static void assertThrownMessage(String msg, String fle) {
		assertThrownMessage(false, msg, fle);
	}

	/**
	 * Assert that a ReplParseException is thrown with a given message.
	 *
	 * @param logMsg
	 * 		Log the exception message.
	 * @param msg
	 * 		The message.
	 * @param fle
	 * 		The file to load input from.
	 */
	public static void assertThrownMessage(boolean logMsg, String msg, String fle) {
		try (FileInputStream fis = new FileInputStream(fle); Scanner scn = new Scanner(fis)) {
			ReplPair.readList(new ArrayList<>(), scn);

			assertTrue(false);
		} catch (ReplParseException rpex) {
			if (logMsg) System.err.println(rpex.toPrintString());

			assertEquals(msg, rpex.toPrintString());
		} catch (Exception ex) {
			System.err.println("EXCEPTION");
			ex.printStackTrace();

			assertTrue(false);

			return;
		}
	}

	public static void assertMultiReplace(String fle, String... inps) {
		assertMultiReplace(false, fle, inps);
	}

	public static void assertMultiReplace(boolean logRep, String fle, String... inps) {
		if (inps.length < 2) throw new IllegalArgumentException("ERROR: Must provide at least two strings to assertMultiReplace");
		if (inps.length % 2 != 0) throw new IllegalArgumentException("ERROR: Odd number of strings passed to assertMultiReplace");

		List<ReplPair> lrp = null;

		try (FileInputStream fis = new FileInputStream(fle); Scanner scn = new Scanner(fis)) {
			lrp = ReplPair.readList(scn);
		} catch (ReplParseException rpex) {
			System.err.println(rpex.toPrintString());
			
			assertTrue(false);
		} catch (Exception ex) {
			System.err.println("EXCEPTION");
			ex.printStackTrace();

			assertTrue(false);

			return;
		}

		for (int i = 0; i < inps.length; i += 2) {
			String right = inps[i];
			String inp   = inps[i + 1];

			assertReplacesTo(logRep, right, lrp, inp);
		}
	}

	public static void assertReplacesFrom(String right, String inp, String fle) {
		assertMultiReplace(fle, right, inp);
	}

	public static void assertReplacesTo(String right, List<ReplPair> rps, String inp) {
		assertReplacesTo(false, right, rps, inp);
	}

	public static void assertReplacesTo(boolean logRep, String right, List<ReplPair> rps, String inp) {
		if (logRep) System.err.printf("\t[LOG] Checking '%s' -> '%s'\n", inp, right);

		String tmp = inp;

		for (ReplPair rp : rps) {
			String oldTmp = tmp;

			tmp = rp.apply(tmp);

			if (logRep) System.err.printf("\t[LOG] '%s' -> '%s'\t%s\n", oldTmp, tmp, rp);
		}

		assertEquals(right, tmp);
	}
}
