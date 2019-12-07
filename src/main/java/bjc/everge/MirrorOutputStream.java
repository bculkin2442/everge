package bjc.everge;

import java.io.*;
import java.util.*;

public class MirrorOutputStream extends OutputStream {
	private List<OutputStream> streams;

	public MirrorOutputStream(OutputStream... strams) {
		streams = new ArrayList<>();

		for (OutputStream stram : strams) {
			streams.add(stram);
		}
	}

	public void close() throws IOException {
		for (OutputStream stream : streams) {
			stream.close();
		}
	}

	public void flush() throws IOException {
		for (OutputStream stream : streams) {
			stream.flush();
		}
	}

	public void write(byte[] ba) throws IOException {
		for (OutputStream stream : streams) {
			stream.write(ba);
		}
	}

	public void write(byte[] ba, int off, int len) throws IOException {
		for (OutputStream stream : streams) {
			stream.write(ba, off, len);
		}
	}

	public void write(int b) throws IOException {
		for (OutputStream stream : streams) {
			stream.write(b);
		}
	}
}
