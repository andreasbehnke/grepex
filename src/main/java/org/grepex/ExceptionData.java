package org.grepex;

import org.apache.commons.lang3.StringUtils;

public class ExceptionData {

	private final Stacktrace stacktrace;
	
	private final String contextLogs;
	
	private final int lineNumberOfFirstOccurrence;
	                                   
	private int numberOfOccurrence;

	public ExceptionData(Stacktrace stacktrace) {
		this.stacktrace = stacktrace;
		this.contextLogs = StringUtils.join(stacktrace.getContext(), '\n');
		this.lineNumberOfFirstOccurrence = stacktrace.getLineNumber();
		this.numberOfOccurrence = 1;
	}

	public Stacktrace getStacktrace() {
		return stacktrace;
	}

	public String getContextLogs() {
		return contextLogs;
	}

	public int getLineNumberOfFirstOccurrence() {
		return lineNumberOfFirstOccurrence;
	}

	public int getNumberOfOccurrence() {
		return numberOfOccurrence;
	}

	public void incrementNumberOfOccurrence() {
		numberOfOccurrence++;
	}

	public void dump(boolean displayNumberOfOccurrences) {
		System.out.println(StandardOptions.SEPARATOR);
		if (displayNumberOfOccurrences) {
			System.out.println(String.format("Found exception %s times with first occurrence at line %s:", numberOfOccurrence, lineNumberOfFirstOccurrence));
		} else {
			System.out.println(String.format("Found exception with first occurrence at line %s:", lineNumberOfFirstOccurrence));
		}
		System.out.println(contextLogs);
		for (String line : stacktrace.getLines()) {
			System.out.println(line);
		}
	}
}
