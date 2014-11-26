package org.grepex;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;

import org.apache.commons.collections.buffer.CircularFifoBuffer;

public class ExceptionParser {

	private final LineNumberReader input;
	
	private final CircularFifoBuffer lineBuffer;
	
	private enum State {
		searchingException,
		processingException
	}
	
	private static State state = State.searchingException;
	
	private Stacktrace currentStacktrace = null;
	
	public ExceptionParser(Reader in, int contextLength) {
		input = new LineNumberReader(in);
		lineBuffer = new CircularFifoBuffer(contextLength + 2);
	}
	
	// returns next stacktrace found or null if no stacktrace was found and EOF reached
	public Stacktrace next() throws IOException {
		String line;
		Stacktrace stacktrace = null;
		while (stacktrace == null && (line = input.readLine()) != null) {
			lineBuffer.add(line);
			stacktrace = processLine(line);
		}
		return stacktrace;
	}
	
	private Stacktrace processLine(String line) {
		Stacktrace stacktrace = null;
		switch (state) {
		case searchingException:
			currentStacktrace = Stacktrace.findStacktraceStart(line, lineBuffer);
			if (currentStacktrace != null) {
				state = State.processingException;
			}
			break;
		case processingException:
			if (!currentStacktrace.addLine(line)) {
				// end of stacktrace reached
				stacktrace = currentStacktrace;
				state = State.searchingException;
			}
			break;
		default:
			break;
		}
		return stacktrace;
	}
}
