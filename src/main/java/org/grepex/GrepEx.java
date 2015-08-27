package org.grepex;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.cli.ParseException;

public class GrepEx {

	private static final int DIFFERENT_STACKTRACE_LINES_THRESHOLD = 3;
	
	private static StandardOptions options;
	
	private static final List<ExceptionData> exceptions = new ArrayList<>();

	public static void main(String[] args) throws IOException, ParseException {
		options = new StandardOptions(args);
		if (options.isDisplayHelp()) {
			options.printHelp();
			return;
		}
		
		ExceptionParser parser = new ExceptionParser(openInput(), options);
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
	
	private static Reader openInput() throws FileNotFoundException {
		InputStream input = null;
		if (options.getInputFileNames().size() > 0) {
			final List<String> fileStack = new ArrayList<>(options.getInputFileNames());
			input = new SequenceInputStream(new Enumeration<InputStream>() {

				@Override
				public boolean hasMoreElements() {
					return !fileStack.isEmpty();
				}

				@Override
				public InputStream nextElement() {
					try {
						String fileName = fileStack.remove(0);
						System.out.println("\nReading file " + fileName + " ...\n");
						return new FileInputStream(fileName);
					} catch (FileNotFoundException e) {
						throw new RuntimeException(e);
					}
				}
				
			});
		} else {
			input = System.in;
		}
		return new InputStreamReader(input);
	}
	
	private static ExceptionData findMatchingException(Stacktrace currentStacktrace) {
		ExceptionData matchedException = null;
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
				matchedException = exceptionData;
				break;
			}
		}
		return matchedException;
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