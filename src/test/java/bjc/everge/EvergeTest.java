package bjc.everge;

import bjc.everge.TestUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static bjc.everge.TestUtils.*;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Unit tests for Everge front-end.
 *
 * @author Ben Culkin.
 */
public class EvergeTest {
	@Test
	public void testConstruct() {
		Everge evg = new Everge();

		assertTrue(true);
	}

	@Test
	public void testArgs() {
		Everge evg = new Everge();

		List<String> errs = new ArrayList<>();

		boolean stat = evg.processArgs(errs, "-v");
		if (!stat) {
			for (String err : errs) {
				System.err.println(err);
			}

			assertTrue(false);
		}
	}

	@Test
	public void testLoad() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		Everge evg = new Everge();
		evg.outStream = new PrintStream(baos);

		List<String> errs = new ArrayList<>();
		boolean stat = evg.processArgs(errs, "-vv", "--file", "data/test/evg-test1.rp",
				"data/test/evg-test1.inp");
		if (!stat) {
			System.err.println("[ERROR] Did not succesfully process args");
			for (String err : errs) {
				System.err.println(err);
			}

			assertTrue(false);
		}

		String outp = baos.toString().trim();
		assertEquals("b\nb", outp);
	}
}
