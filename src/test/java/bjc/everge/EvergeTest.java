package bjc.everge;

import bjc.everge.*;

import java.io.*;

import java.util.*;

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
		ByteArrayOutputStream errBaos = new ByteArrayOutputStream();

		OutputStream normOut = new MirrorOutputStream(baos, System.out);
		OutputStream normErr = new MirrorOutputStream(errBaos, System.err);

		Everge evg = new Everge();
		evg.setOutput(normOut);
		evg.setError(normErr);

		//evg.processArgs("--verbosity", "3");
		List<String> errs = new ArrayList<>();
		boolean stat = evg.processArgs(errs, "--input-status",  "line", "--file", "data/test/evg-test1.rp",
				"data/test/evg-test1.inp");
		if (!stat) {
			System.err.println("[ERROR] Did not succesfully process args");
			for (String err : errs) {
				System.err.println(err);
			}
			System.err.println("[ERROR] Normal Output:\n--------------------");
			System.err.println(baos.toString().trim());
			System.err.println("--------------------\n[ERROR] Error Output:\n------------------");
			System.err.println(errBaos.toString().trim());
			System.err.println("--------------------");

			assertTrue(false);
		}

		String outp = baos.toString().trim();
		assertEquals("b\nb", outp);
	}
}
