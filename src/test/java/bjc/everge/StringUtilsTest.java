package bjc.everge;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import static bjc.everge.TestUtils.*;

import static org.junit.Assert.*;

/**
 * Test for StringUtils.
 *
 * @author Ben Culkin
 */
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

}
