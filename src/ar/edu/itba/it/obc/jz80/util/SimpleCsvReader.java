package ar.edu.itba.it.obc.jz80.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SimpleCsvReader extends InputStream {
	
	InputStream stream;
	
	public SimpleCsvReader(InputStream stream) {
		this.stream = stream;
	}
	
	public SimpleCsvReader(File file) throws FileNotFoundException {
		this(new FileInputStream(file));
	}
	
	public SimpleCsvReader(String filename) throws FileNotFoundException {
		this(new File(filename));
	}
	
	public List<String> getNextCsvRow() throws IOException {
		List<String> row = new ArrayList<String>();
		StringBuffer currentValue = new StringBuffer();
		boolean eof = true;
		int state = 0;
		// Finite state machine
		while (true) {
			int c = stream.read();
			eof &= (c == -1);
			if (c == -1 || (state != 2 && c == '\n')) {
				row.add(currentValue.toString());
				return eof ? null : row;
			}
			switch (state) {
			case 0: // row start
				if (c == ',') {
					row.add(currentValue.toString());
					currentValue = new StringBuffer();
				} else if (c == '"') {
					state = 2;
				} else {
					currentValue.append((char) c);
					state = 1;
				}
				break;
			case 1: // plain text
				if (c == ',') {
					row.add(currentValue.toString());
					currentValue = new StringBuffer();
					state = 0;
				} else {
					currentValue.append((char) c);
				}
				break;
			case 2: // quoted text
				if (c == '"') {
					state = 3;
				} else {
					currentValue.append((char) c);
				}
				break;
			case 3: // quoted text closing
				if (c == '"') {
					currentValue.append((char) c);
					state = 2;
				} else if (c == ',') {
					row.add(currentValue.toString());
					currentValue = new StringBuffer();
					state = 0;
				} else {
					currentValue.append((char) c);
				}
				break;
			}
		}
	}

	@Override
	public int read() throws IOException {
		return stream.read();
	}

	@Override
	public int available() throws IOException {
		return stream.available();
	}

	@Override
	public void close() throws IOException {
		stream.close();
	}

	@Override
	public synchronized void reset() throws IOException {
		stream.reset();
	}

}
