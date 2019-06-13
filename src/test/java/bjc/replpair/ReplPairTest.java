package bjc.replpair;

import java.io.FileInputStream;
import java.io.IOException;

import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Unit test for ReplPair.
 *
 * @author Ben Culkin
 */
public class ReplPairTest {
	@Test
	public void testLoadFile() {
		List<ReplPair> lrp = null;
		String fName = "data/test/test1.rp";

		try (FileInputStream fis = new FileInputStream(fName); Scanner scn = new Scanner(fis)) {
			lrp = ReplPair.readList(scn);

			assertTrue(lrp.size() == 0);
		} catch (IOException ioex) {
			assertTrue(false);
		}
	}

	@Test
	public void testSingleReplace() {
		assertMultiReplace("data/test/test2.rp", "test2", "test1", "test2", "test2");
	}

	@Test
	public void testMultiReplace() {
		assertMultiReplace("data/test/test3.rp", "A B C", "a b c", "A A B", "a a b", "AAB", "aab");
	}

	@Test
	public void testReplaceOrder() {
		assertMultiReplace("data/test/test4.rp", "a", "a", "d", "ab");
	}

	@Test
	public void testReplaceExpOrder() {
		assertMultiReplace("data/test/test5.rp", "a", "a", "aa", "ab");
	}

	@Test
	public void testStaging() {
		assertMultiReplace(true, "data/test/test6.rp", "c", "a", "y2", "x");
	}

	private void assertMultiReplace(String fle, String... inps) {
		assertMultiReplace(false, fle, inps);
	}

	private void assertMultiReplace(boolean logRep, String fle, String... inps) {
		if (inps.length < 2) throw new IllegalArgumentException("ERROR: Must provide at least two strings to assertMultiReplace");
		if (inps.length % 2 != 0) throw new IllegalArgumentException("ERROR: Odd number of strings passed to assertMultiReplace");

		List<ReplPair> lrp = null;

		try (FileInputStream fis = new FileInputStream(fle); Scanner scn = new Scanner(fis)) {
			lrp = ReplPair.readList(scn);
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

	private void assertReplacesFrom(String right, String inp, String fle) {
		assertMultiReplace(fle, right, inp);
	}

	private void assertReplacesTo(String right, List<ReplPair> rps, String inp) {
		assertReplacesTo(false, right, rps, inp);
	}

	private void assertReplacesTo(boolean logRep, String right, List<ReplPair> rps, String inp) {
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
