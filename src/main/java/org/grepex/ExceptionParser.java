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

	private final List<String> includes;

	public ExceptionParser(Reader in, StandardOptions options) {
		input = new LineNumberReader(in);
		lineBuffer = new CircularFifoBuffer(options.getContextLineCount() + 2);
		this.includes = options.getIncludes();
		this.excludes = options.getExcludes();
	}

	protected ExceptionParser(Reader in, int contextLineCount, List<String> includes, List<String> excludes) {
		input = new LineNumberReader(in);
		lineBuffer = new CircularFifoBuffer(contextLineCount + 2);
		this.includes = includes;
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
		boolean match = false;
		boolean isIncluded = true;
		if (includes.size() > 0) {
			isIncluded = false;
			for (String include : includes) {
				for (String exception : stacktrace.getCauses()) {
					if (exception.contains(include)) {
						isIncluded = true;
						break;
					}
				}
				if (isIncluded) {
					break;
				}
			}
		}
		if (isIncluded) {
			match = true;
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
		}
		return match;
	}
}
