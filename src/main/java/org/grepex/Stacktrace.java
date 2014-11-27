package org.grepex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Stacktrace {
	
	private final static String EXCEPTION_INDICATOR = "Exception";
	
	private final List<String> context;
	
	private final int lineNumber;

	private final List<String> lines = new ArrayList<>();
	
	private final List<String> causes = new ArrayList<>();
	
	public Stacktrace(List<String> context, String exception, int lineNumber) {
		this.context = context;
		causes.add(exception);
		lines.add(exception);
		this.lineNumber = lineNumber;
	}
	
	public List<String> getContext() {
		return context;
	}

	public List<String> getLines() {
		return lines;
	}
	
	public List<String> getCauses() {
		return causes;
	}
	
	public int getLineNumber() {
		return lineNumber;
	}
	
	private static boolean startsWithWhitespace(String line) {
		return (line != null && line.length() > 0 && Character.isWhitespace(line.charAt(0)));
	}
	
	// internal API, only used by exception parser
	static Stacktrace findStacktraceStart(String currentLine, int currentLineNumber, Collection lineBuffer) {
		if (startsWithWhitespace(currentLine)) {
			// whitespace is an indicator for an exception stacktrace
			List<String> context = new ArrayList<>(lineBuffer);
			int contextSize = context.size();
			if (contextSize > 2) {
				// if line before whitespace contains word 'Exception', than we have hit an exception stacktrace
				String exception = context.get(contextSize -2);
				if (exception.contains(EXCEPTION_INDICATOR)) {
					Stacktrace stacktrace = new Stacktrace(context.subList(0, contextSize - 2), exception, currentLineNumber - 2);
					stacktrace.addLine(context.get(contextSize - 2));
					stacktrace.addLine(context.get(contextSize - 1));
					return stacktrace;
				}
			}
		}
		// No stacktrace found
		return null;
	}
	
	// internal API, only used by exception parser
	boolean addLine(String line) {
		boolean lineAdded = false;
		boolean startsWithAt = line.trim().startsWith("at");
		boolean isCause = (line != null && line.startsWith("Caused by"));
		if (startsWithAt || isCause) {
			lines.add(line);
			if (isCause) {
				causes.add(line);
			}
			lineAdded = true;
		}
		return lineAdded;
	}
}
