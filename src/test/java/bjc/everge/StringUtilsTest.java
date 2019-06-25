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
		assertSplitsTo("a /  b/c", "/", " ", "a", "/ ", "b/c");
	}

	@Test
	public void testEdgeSplit() {
		// Starting with the delimiter doesn't create a blank string
		assertSplitsTo("/a", "|", "/", "", "a");
		assertSplitsTo("a/", "|", "/", "a");
	}

	private void assertSplitsTo(String inp, String esc, String splat, String... right) {
		try {
			String[] lst = StringUtils.escapeSplit(esc, splat, inp);

			assertArrayEquals(right, lst);
		} catch (Exception ex) {
			System.err.println("EXCEPTION");
			ex.printStackTrace();
			System.err.println();

			assertTrue(false);
		}
	}
}
