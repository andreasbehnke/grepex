package org.grepex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Stacktrace {
	
	private final static String EXCEPTION_INDICATOR = "Exception";
	
	private final List<String> context;

	private final List<String> lines = new ArrayList<>();
	
	private final List<String> causes = new ArrayList<>();
	
	public Stacktrace(List<String> context, String exception) {
		this.context = context;
		causes.add(exception);
		lines.add(exception);
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
	
	private static boolean startsWithWhitespace(String line) {
		return (line != null && line.length() > 0 && Character.isWhitespace(line.charAt(0)));
	}
	
	public static Stacktrace findStacktraceStart(String currentLine, Collection lineBuffer) {
		if (startsWithWhitespace(currentLine)) {
			// whitespace is an indicator for an exception stacktrace
			List<String> context = new ArrayList<>(lineBuffer);
			int contextSize = context.size();
			if (contextSize > 2) {
				// if line before whitespace contains word 'Exception', than we have hit an exception stacktrace
				String exception = context.get(contextSize -2);
				if (exception.contains(EXCEPTION_INDICATOR)) {
					Stacktrace stacktrace = new Stacktrace(context.subList(0, contextSize - 2), exception);
					stacktrace.addLine(context.get(contextSize - 2));
					stacktrace.addLine(context.get(contextSize - 1));
					return stacktrace;
				}
			}
		}
		// No stacktrace found
		return null;
	}
	
	public boolean addLine(String line) {
		boolean lineAdded = false;
		boolean startsWithWhitespace = startsWithWhitespace(line);
		boolean isCause = (line != null && line.startsWith("Caused by"));
		if (startsWithWhitespace || isCause) {
			lines.add(line);
			if (isCause) {
				causes.add(line);
			}
			lineAdded = true;
		}
		return lineAdded;
	}
}
