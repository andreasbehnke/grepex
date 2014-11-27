package org.grepex;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.cli.ParseException;

public class GrepEx {

	private static final int CONTEXT_LINE_COUNT = 10;
	
	private static final int DIFFERENT_STACKTRACE_LINES_THRESHOLD = 3;
	
	private static StandardOptions options;
	
	private static final List<ExceptionData> exceptions = new ArrayList<>();

	public static void main(String[] args) throws IOException, ParseException {
		options = new StandardOptions(args);
		if (options.isDisplayHelp()) {
			options.printHelp();
			return;
		}
		
		ExceptionParser parser = new ExceptionParser(new InputStreamReader(System.in), CONTEXT_LINE_COUNT);
		Stacktrace stacktrace;
		while((stacktrace = parser.next()) != null) {
			ExceptionData exceptionData = findMatchingException(stacktrace);
			if (exceptionData == null) {
				exceptionData = new ExceptionData(stacktrace);
				exceptions.add(exceptionData);
				if (!options.isDisplaySummary()) {
					exceptionData.dump(false);
				}
			} else {
				exceptionData.incrementNumberOfOccurrence();
			}
		}
		
		System.out.println();
		System.out.println(StandardOptions.SEPARATOR);
		System.out.println("Summary:");
		System.out.println(String.format("Found %s unique exception stacktraces in input stream.", exceptions.size()));
		if (options.isDisplaySummary()) {
			displaySummary();
		}
	}
	
	private static ExceptionData findMatchingException(Stacktrace currentStacktrace) {
		for (ExceptionData exceptionData : exceptions) {
			boolean match = false;
			Stacktrace stacktrace = exceptionData.getStacktrace();
			int lineDiff  = Math.abs(currentStacktrace.getLines().size() - stacktrace.getLines().size());
			if (lineDiff == 0) {
				int differentLines = 0;
				List<String> currentLines = currentStacktrace.getLines();
				List<String> lines = stacktrace.getLines();
				match = true;
				for(int i = 0; i < lines.size(); i++) {
					if (!lines.get(i).equals(currentLines.get(i))) {
						differentLines++;
					}
					if (differentLines > DIFFERENT_STACKTRACE_LINES_THRESHOLD) {
						match = false;
						break;
					}
				}
			}
			if (match) {
				return exceptionData;
			}
		}
		return null;
	}
	
	private static void displaySummary() {
		ArrayList<ExceptionData> exceptionsDataList = new ArrayList<>(exceptions);
		Collections.sort(exceptionsDataList, new Comparator<ExceptionData>() {
			@Override
			public int compare(ExceptionData o1, ExceptionData o2) {
				return o2.getNumberOfOccurrence() - o1.getNumberOfOccurrence();
			}
		});
		for (ExceptionData exceptionData : exceptionsDataList) {
			exceptionData.dump(true);
		}
	}
}