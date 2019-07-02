package bjc.everge;

import bjc.everge.ControlledString.Control;

import org.junit.Test;

import static bjc.everge.ControlledString.Control.C;
import static bjc.everge.TestUtils.*;

import static org.junit.Assert.*;

/**
 * Test for ControlledString.
 *
 * @author Ben Culkin
 */
public class ControlledStringTest {
	@Test
	public void testNoControls() {
		assertIsControl("a", "a");
		assertIsControl("abc", "abc");
	}

	@Test
	public void testSimpleControls() {
		assertIsControl("//a//", "", C("a"));
		assertIsControl("//a;b//", "", C("a"), C("b"));
		assertIsControl("//a;b;c//", "", C("a"), C("b"), C("c"));
	}

	@Test
	public void testArgedControls() {
		assertIsControl("//a/b//", "", C("a", "b"));
		assertIsControl("//a/b;c//", "", C("a", "b"), C("c"));
		assertIsControl("//a/b;c/1/2//", "", C("a", "b"), C("c", "1", "2"));
	}

	@Test
	public void testMixedControls() {
		assertIsControl("//a//b", "b", C("a"));
	}
}
