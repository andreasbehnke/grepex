package org.grepex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.buffer.CircularFifoBuffer;

public class GrepEx {

	private static final int CONTEXT_LINE_COUNT = 10;
	
	private static final String HELP = "grepex [-s|--summary] [-h|--help]" + System.lineSeparator()
			+ "\t-s --summary: Print all unique exceptions after processing input ordered by number of occurrences." + System.lineSeparator()
			+ "\t-h --help: print this help message";
	
	private static final String SUMMARY_OPTION_SHORT = "-s";
	
	private static final String SUMMARY_OPTION_LONG = "--summary";
	
	private static final String HELP_OPTION_SHORT = "-h";
	
	private static final String HELP_OPTION_LONG = "--help";
	
	private static boolean displaySummary = false;
	
	private enum State {
		searchingException,
		processingException
	}
	
	private static State state = State.searchingException;
	
	private static int linenumber = 1;
	
	private static CircularFifoBuffer lineBuffer = new CircularFifoBuffer(CONTEXT_LINE_COUNT);
	
	private static int currentExceptionLineNumber;
	
	private static String currentExceptionContext;
	
	private static StringBuilder currentExceptionStack;
	
	private static final Map<String, ExceptionData> exceptionMap = new HashMap<>();

	public static void main(String[] args) throws IOException {
		if (args.length > 0) {
			if (args[0].equals(HELP_OPTION_LONG) || args[0].equals(HELP_OPTION_SHORT)) {
				System.out.println(HELP);
				return;
			}
			if (args[0].equals(SUMMARY_OPTION_LONG) || args[0].equals(SUMMARY_OPTION_SHORT)) {
				displaySummary = true;
			}
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
		System.out.println(String.format("Found %s unique exception stacktraces in input stream:", exceptionMap.size()));
		if (displaySummary) {
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
				currentExceptionStack = new StringBuilder();
				state = State.processingException;
			}
			break;
		case processingException:
			if (line.length() > 0 && 
					(Character.isWhitespace(line.charAt(0)) || line.startsWith("Caused by"))) {
				// additional stacktrace line
				currentExceptionStack.append(System.lineSeparator()).append(line);
			} else {
				// end of exception
				String exceptionStackTrace = currentExceptionStack.toString();
				ExceptionData exceptionData = exceptionMap.get(exceptionStackTrace);
				if (exceptionData == null) {
					exceptionData = new ExceptionData(exceptionStackTrace, currentExceptionContext, currentExceptionLineNumber);
					exceptionMap.put(exceptionStackTrace, exceptionData);
					if (!displaySummary) {
						exceptionData.dump(false);
					}
				} else {
					exceptionData.incrementNumberOfOccurrence();
				}
				state = State.searchingException;
			}
			break;
		default:
			break;
		}
	}
	
	private static void displaySummary() {
		ArrayList<ExceptionData> exceptionsDataList = new ArrayList<>(exceptionMap.values());
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