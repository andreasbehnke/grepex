package org.grepex;

public class ExceptionData {

	private final String stacktrace;
	
	private final String contextLogs;
	
	private final int lineNumberOfFirstOccurrence;
	                                   
	private int numberOfOccurrence;

	public ExceptionData(String stacktrace, String contextLogs, int lineNumberOfFirstOccurrence) {
		this.stacktrace = stacktrace;
		this.contextLogs = contextLogs;
		this.lineNumberOfFirstOccurrence = lineNumberOfFirstOccurrence;
		this.numberOfOccurrence = 1;
	}

	public String getStacktrace() {
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
		System.out.println(stacktrace);
	}
}
