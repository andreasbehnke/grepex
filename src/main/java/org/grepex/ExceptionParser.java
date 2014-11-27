package org.grepex;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.List;

import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.apache.commons.lang3.StringUtils;

public class ExceptionParser {

	private final LineNumberReader input;
	
	private final CircularFifoBuffer lineBuffer;
	
	private enum State {
		searchingException,
		processingException
	}
	
	private static State state = State.searchingException;
	
	private Stacktrace currentStacktrace = null;
	
	private final List<String> excludes;
	
	public ExceptionParser(Reader in, int contextLength, List<String> excludes) {
		input = new LineNumberReader(in);
		lineBuffer = new CircularFifoBuffer(contextLength + 2);
		this.excludes = excludes;
	}
	
	// returns next stacktrace found or null if no stacktrace was found and EOF reached
	public Stacktrace next() throws IOException {
		String line;
		Stacktrace stacktrace = null;
		while (stacktrace == null && (line = input.readLine()) != null) {
			if (!StringUtils.isEmpty(line)) {
				lineBuffer.add(line);
				stacktrace = processLine(line);
			}
		}
		return stacktrace;
	}
	
	private Stacktrace processLine(String line) {
		Stacktrace stacktrace = null;
		switch (state) {
		case searchingException:
			currentStacktrace = Stacktrace.findStacktraceStart(line, input.getLineNumber(), lineBuffer);
			if (currentStacktrace != null) {
				state = State.processingException;
			}
			break;
		case processingException:
			if (!currentStacktrace.addLine(line)) {
				// end of stacktrace reached
				if (match(currentStacktrace)) {
					stacktrace = currentStacktrace;
				}
				state = State.searchingException;
			}
			break;
		default:
			break;
		}
		return stacktrace;
	}
	
	private boolean match(Stacktrace stacktrace) {
		boolean match = true;
		for (String exclude : excludes) {
			for (String exception : stacktrace.getCauses()) {
				if (exception.contains(exclude)) {
					match = false;
					break;
				}
			}
			if (!match) {
				break;
			}
		}
		return match;
	}
}
