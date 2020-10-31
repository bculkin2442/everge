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
@SuppressWarnings("javadoc")
public class ReplPairTest {
	@Test
	public void testLoadFile() {
		List<ReplPair> lrp = null;
		String fName = "data/test/test1-empty.rp";

		try (FileInputStream fis = new FileInputStream(fName);
				Scanner scn = new Scanner(fis)) {
			ReplPairParser parser = new ReplPairParser();
			
			lrp = parser.readList(scn);

			assertTrue(lrp.size() == 0);
		} catch (IOException ioex) {
			assertTrue(false);
		}
	}

	@Test
	public void testSingleReplace() {
		assertMultiReplace("data/test/test2-singlereplace.rp", "test2", "test1", "test2", "test2");
	}

	@Test
	public void testMultiReplace() {
		assertMultiReplace("data/test/test3-multireplace.rp", "A B C", "a b c", "A A B", "a a b",
				"AAB", "aab");
	}

	@Test
	public void testReplaceOrder() {
		assertMultiReplace("data/test/test4-implicitorder.rp", "a", "a", "d", "ab");
	}

	@Test
	public void testReplaceExpOrder() {
		assertMultiReplace(false, "data/test/test5-explicitorder.rp", "a", "a", "aa", "ab");
	}

	@Test
	public void testStaging() {
		assertMultiReplace("data/test/test6-staging.rp", "c", "a", "y2", "x");
	}

	@Test
	public void testErrorException() {
		String msg = "[ERROR] An error occured parsing replacement pairs:"
				+ "\n\t[ERROR] line 2, pair 1: Ran out of input looking for"
				+ " replacement body for raw name 'a'"
				+ "\n\t\tContext: No associated line";

		assertThrownMessage(msg, "data/test/test7-error.rp");
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
		assertMultiReplace("data/test/test8-globals.rp", "b1d\n1d\n1b1", "acca");
	}

	@Test
	public void testGuards() {
		assertMultiReplace("data/test/test10-guard.rp", "a", "a", "bbb", "aaa");
	}
	
	@Test
	public void testMultiControl() {
		assertMultiReplace("data/test/test11-bodyinlinemulti.rp", "bc", "a", "z\na", "d", "m", "po");
		
		// NOTE Uncomment when :EndingSlash is fixed 
		//assertMultiReplace("data/test/test11.rp", "q\\", "FG");
	}
}
