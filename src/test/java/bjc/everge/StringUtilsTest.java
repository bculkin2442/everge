package bjc.everge;

import org.junit.Test;

import static bjc.everge.TestUtils.*;

import static org.junit.Assert.*;

/**
 * Test for StringUtils.
 *
 * @author Ben Culkin
 */
@SuppressWarnings("javadoc")
public class StringUtilsTest {
	@Test
	public void testNullSplit() {
		assertSplitsTo("a", null, " ", "a");
		assertSplitsTo("a b", null, " ", "a", "b");
		assertSplitsTo("a b cd", null, " ", "a", "b", "cd");
	}

	@Test
	public void testNoEscapeSplit() {
		assertSplitsTo("a", "/", " ", "a");
		assertSplitsTo("a b", "/", " ", "a", "b");
		assertSplitsTo("a b/c", "/", " ", "a", "b/c");
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

	@Test
	public void testSliceString() {
		assertEquals("ac", StringUtils.sliceString("abc", 1, 2));
		assertEquals("ac", StringUtils.sliceStringL("abc", 1, 1));
	}
}
