package org.grepex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.buffer.CircularFifoBuffer;

public class GrepEx {

	private static final int CONTEXT_LINE_COUNT = 10;
	
	private enum State {
		searchingException,
		processingException
	}
	
	private static State state = State.searchingException;
	
	private static int linenumber = 1;
	
	private static CircularFifoBuffer lineBuffer = new CircularFifoBuffer(CONTEXT_LINE_COUNT);
	
	private static String currentExceptionContext;
	
	private static StringBuilder currentExceptionStack;
	
	private static final Set<String> exceptionStacks = new HashSet<>();

	public static void main(String[] args) throws IOException {
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
		String line;
		while ((line = input.readLine()) != null) {
			lineBuffer.add(line);
			processLine(line);
			linenumber++;
		}
	}

	private static void processLine(String line) {
		switch (state) {
		case searchingException:
			if (line.contains("Exception")) {
				StringBuilder firstExceptionLines = new StringBuilder(String.format("\n*****\nFound exception at line %s:", linenumber));
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
				if (exceptionStacks.add(exceptionStackTrace)) {
					System.out.println(currentExceptionContext);
					System.out.println(exceptionStackTrace);
				}
				state = State.searchingException;
			}
			break;
		default:
			break;
		}
	}
}