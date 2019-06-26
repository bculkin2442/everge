package bjc.everge;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;

public class StringUtilsTest {
	@Test
	public void testNullSplit() {
		assertSplitsTo("a", null, " ", "a");	
		assertSplitsTo("a b", null, " ", "a", "b");	
		assertSplitsTo("a b cd", null, " ", "a", "b", "cd");
	}

	@Test
	public void testNoEscapeSplit() {
		assertSplitsTo("a",        "/", " ", "a");
		assertSplitsTo("a b",      "/", " ", "a", "b");
		assertSplitsTo("a b/c",    "/", " ", "a", "b/c");
	}

	@Test
	public void testEscapeSplit() {
		assertSplitsTo("a|/||b/c", "|", "/", "a/|b", "c");

		assertSplitsTo("a||/b", "|", "/", "a|", "b");
	}

	@Test
	public void testLongSplit() {
		assertSplitsTo("a||b||c", " ", "||", "a", "b", "c");

		assertSplitsTo("a&&||b||c", "&&", "||", "a||b", "c");

		assertSplitsTo("a&&&&||b||c", "&&", "||", "a&&", "b", "c");

		assertSplitsTo("a&&&&&&||b||c", "&&", "||", "a&&||b", "c");
	}

	@Test
	public void testEdgeSplit() {
		// Starting with the delimiter doesn't create a blank string
		assertSplitsTo("/a", "|", "/", "", "a");
		assertSplitsTo("a/", "|", "/", "a");
	}

	private void assertSplitsTo(String inp, String esc, String splat, String... right) {
		assertSplitsTo(false, inp, esc, splat, right);
	}

	private void assertSplitsTo(boolean doLog, String inp, String esc, String splat, String... right) {
		try {
			if (doLog) StringUtils.isDebug = true;

			String[] lst = StringUtils.escapeSplit(esc, splat, inp);

			if (doLog) {
				System.err.printf("[TRACE] Returned ");

				for (String str : lst) {
					System.err.printf("(%s) ", str);
				}

				System.err.println();
			}

			assertArrayEquals(right, lst);
		} catch (Exception ex) {
			System.err.println("EXCEPTION");
			ex.printStackTrace();
			System.err.println();

			assertTrue(false);
		} finally {
			if (doLog) StringUtils.isDebug = false;
		}
	}
}
