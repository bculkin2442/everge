package bjc.everge;

import java.io.FileInputStream;
import java.io.IOException;

import java.util.List;
import java.util.Scanner;

import static bjc.everge.TestUtils.*;

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

		try (FileInputStream fis = new FileInputStream(fName);
				Scanner scn = new Scanner(fis)) {
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
		assertMultiReplace("data/test/test3.rp", "A B C", "a b c", "A A B", "a a b",
				"AAB", "aab");
	}

	@Test
	public void testReplaceOrder() {
		assertMultiReplace("data/test/test4.rp", "a", "a", "d", "ab");
	}

	@Test
	public void testReplaceExpOrder() {
		assertMultiReplace(false, "data/test/test5.rp", "a", "a", "aa", "ab");
	}

	@Test
	public void testStaging() {
		assertMultiReplace("data/test/test6.rp", "c", "a", "y2", "x");
	}

	@Test
	public void testErrorException() {
		String msg = "[ERROR] An error occured parsing replacement pairs:"
				+ "\n\t[ERROR] line 2, pair 1: Ran out of input looking for"
				+ " replacement body for raw name 'a'"
				+ "\n\t\tContext: No associated line";

		assertThrownMessage(msg, "data/test/test7.rp");
	}

	@Test
	public void testPairs() {
		ReplPair rp1 = new ReplPair();
		ReplPair rp2 = new ReplPair("", "");

		rp1.name = rp1.find;

		assertEquals(rp2, rp1);
		assertEquals(rp2.toString(), rp1.toString());
	}

	@Test
	public void testGlobals() {
		assertMultiReplace("data/test/test8.rp", "b1d\n1d\n1b1", "acca");
	}

	@Test
	public void testGuards() {
		assertMultiReplace("data/test/test10.rp", "a", "a", "bbb", "aaa");
	}
}
