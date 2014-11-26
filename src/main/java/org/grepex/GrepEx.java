package org.grepex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.cli.ParseException;
import org.apache.commons.collections.buffer.CircularFifoBuffer;

public class GrepEx {

	private static final int CONTEXT_LINE_COUNT = 10;
	
	private static final int DIFFERENT_STACKTRACE_LINES_THRESHOLD = 3;
	
	private static StandardOptions options;
	
	private enum State {
		searchingException,
		processingException
	}
	
	private static State state = State.searchingException;
	
	private static int linenumber = 1;
	
	private static CircularFifoBuffer lineBuffer = new CircularFifoBuffer(CONTEXT_LINE_COUNT);
	
	private static int currentExceptionLineNumber;
	
	private static String currentExceptionContext;
	
	private static Stacktrace currentExceptionStacktrace;
	
	private static final List<ExceptionData> exceptions = new ArrayList<>();

	public static void main(String[] args) throws IOException, ParseException {
		options = new StandardOptions(args);
		if (options.isDisplayHelp()) {
			options.printHelp();
			return;
		}
		
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
		String line;
		while ((line = input.readLine()) != null) {
			lineBuffer.add(line);
			processLine(line);
			linenumber++;
		}
		System.out.println();
		System.out.println(StandardOptions.SEPARATOR);
		System.out.println("Summary:");
		System.out.println(String.format("Found %s unique exception stacktraces in input stream.", exceptions.size()));
		if (options.isDisplaySummary()) {
			displaySummary();
		}
	}

	private static void processLine(String line) {
		switch (state) {
		case searchingException:
			if (line.contains("Exception")) {
				currentExceptionLineNumber = linenumber;
				StringBuilder firstExceptionLines = new StringBuilder();
				for (Object contextLine : lineBuffer) {
					firstExceptionLines.append(System.lineSeparator()).append(contextLine);
				}
				currentExceptionContext = firstExceptionLines.toString();
				currentExceptionStacktrace = new Stacktrace(null); // TODO, use factory method create and make constructor private
				state = State.processingException;
			}
			break;
		case processingException:
			if (line.length() > 0 && 
					(Character.isWhitespace(line.charAt(0)) || line.startsWith("Caused by"))) {
				// additional stacktrace line
				currentExceptionStacktrace.addLine(line);
			} else {
				// end of exception
				if (!currentExceptionStacktrace.getLines().isEmpty()) {
					ExceptionData exceptionData = findMatchingException(currentExceptionStacktrace);
					if (exceptionData == null) {
						exceptionData = new ExceptionData(currentExceptionStacktrace, currentExceptionContext, currentExceptionLineNumber);
						exceptions.add(exceptionData);
						if (!options.isDisplaySummary()) {
							exceptionData.dump(false);
						}
					} else {
						exceptionData.incrementNumberOfOccurrence();
					}
				}
				state = State.searchingException;
			}
			break;
		default:
			break;
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