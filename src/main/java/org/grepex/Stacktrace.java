package org.grepex;

import java.util.ArrayList;
import java.util.List;

public class Stacktrace {

	private final List<String> lines = new ArrayList<>();
	
	private final List<String> causes = new ArrayList<>();

	public List<String> getLines() {
		return lines;
	}
	
	public List<String> getCauses() {
		return causes;
	}
	
	public void addLine(String line) {
		lines.add(line);
		if (!startsWithWhitespace(line)) {
			causes.add(line);
		}
	}
	
	private boolean startsWithWhitespace(String line) {
		return (line != null && line.length() > 0 && Character.isWhitespace(line.charAt(0)));
	}
}
