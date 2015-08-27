package org.grepex;

import java.io.ObjectInputStream.GetField;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;
import org.apache.commons.lang3.StringUtils;

public class StandardOptions {

	public static final String SEPARATOR = "\n------\n";
	
	public static final int MAX_WIDTH = 120;
	
	private static final String COMMAND_SYNTAX = "grepex [options|--help] [file1 file2 file3 ...]" + System.lineSeparator();
	
	private static final String HELP_OPT = "h";
	
	private static final Option HELP = new Option(HELP_OPT, "help", false, "Print this help message");
	
	private static final String SUMMARY_OPT = "s";
	
	private static final Option SUMMARY = new Option(SUMMARY_OPT, "summary", false, "Print all unique exceptions after processing input ordered by number of occurrences.");
	
	private static final String EXCLUDE_OPT = "e";
	
	private static final Option EXCLUDE = new Option(EXCLUDE_OPT, "exclude", true, "Comma separated list of excludes for exception namespaces");

	private static final String INCLUDE_OPT = "i";

	private static final Option INCLUDE = new Option(INCLUDE_OPT, "include", true, "Comma separated list of includes for exception namespaces");

	private static final Options OPTIONS = new Options()
			.addOption(HELP)
			.addOption(SUMMARY)
			.addOption(EXCLUDE)
			.addOption(INCLUDE);

	private static final int CONTEXT_LINE_COUNT = 10;

	private final boolean displayHelp;
	
	private final boolean displaySummary;
	
	private final List<String> excludes;

	private final List<String> includes;
	
	private final List<String> inputFileNames;
	
	public StandardOptions(String[] args) throws ParseException {
		CommandLine cl = new GnuParser().parse(OPTIONS, args);
		this.displayHelp = cl.hasOption(HELP_OPT);
		this.displaySummary = cl.hasOption(SUMMARY_OPT);
		this.excludes = getOptionList(cl, EXCLUDE_OPT);
		this.includes = getOptionList(cl, INCLUDE_OPT);
		this.inputFileNames = cl.getArgList(); 
	}
	
	private List<String> getOptionList(CommandLine cl, String opt) {
		String listString = cl.getOptionValue(opt);
		if (listString != null) {
			return Arrays.asList(StringUtils.split(listString, ','));
		} else {
			return Collections.EMPTY_LIST;
		}
	}

	public int getContextLineCount() { return CONTEXT_LINE_COUNT; }

	public boolean isDisplaySummary() {
		return displaySummary;
	}
	
	public boolean isDisplayHelp() {
		return displayHelp;
	}

	public List<String> getIncludes() { return includes; }

	public List<String> getExcludes() {
		return excludes;
	}

	public List<String> getInputFileNames() {
		return inputFileNames;
	}
	
	public void printHelp() {
		HelpFormatter helpFormatter = new HelpFormatter();
		helpFormatter.setWidth(MAX_WIDTH);
		helpFormatter.printHelp(COMMAND_SYNTAX, "", OPTIONS, "");
	}
}