package bjc.everge;

import java.io.*;
import java.util.*;

/**
 * An output stream that mirrors its contents to other streams.
 * 
 * @author Ben Culkin
 *
 */
public class MirrorOutputStream extends OutputStream {
	private List<OutputStream> streams;

	/**
	 * Create a new mirroring output stream.
	 * 
	 * @param strams
	 *               The output streams to mirror to.
	 */
	public MirrorOutputStream(OutputStream... strams) {
		streams = new ArrayList<>();

		for (OutputStream stram : strams) {
			streams.add(stram);
		}
	}

	@Override
	public void close() throws IOException {
		for (OutputStream stream : streams) {
			stream.close();
		}
	}

	@Override
	public void flush() throws IOException {
		for (OutputStream stream : streams) {
			stream.flush();
		}
	}

	@Override
	public void write(byte[] ba) throws IOException {
		for (OutputStream stream : streams) {
			stream.write(ba);
		}
	}

	@Override
	public void write(byte[] ba, int off, int len) throws IOException {
		for (OutputStream stream : streams) {
			stream.write(ba, off, len);
		}
	}

	@Override
	public void write(int b) throws IOException {
		for (OutputStream stream : streams) {
			stream.write(b);
		}
	}
}
