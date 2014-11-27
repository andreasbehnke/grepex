package org.grepex;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;

public class StandardOptions {
	
	public static final String SEPARATOR = "\n------\n";
	
	public static final int MAX_WIDTH = 120;
	
	private static final String COMMAND_SYNTAX = "grepex [-s|--summary] [-h|--help]" + System.lineSeparator();
	
	private static final String HELP_OPT = "h";
	
	private static final Option HELP = new Option(HELP_OPT, "help", false, "Print this help message");
	
	private static final String SUMMARY_OPT = "s";
	
	private static final Option SUMMARY = new Option(SUMMARY_OPT, "summary", false, "Print all unique exceptions after processing input ordered by number of occurrences.");
	
	private static final String EXCLUDE_OPT = "e";
	
	private static final Option EXCLUDE = new Option(EXCLUDE_OPT, "exclude", true, "Comma separated list of excludes for exception namespaces");

	private static final Options OPTIONS = new Options().addOption(HELP).addOption(SUMMARY).addOption(EXCLUDE);
	
	private final CommandLine cl;
	
	private final boolean displayHelp;
	
	private final boolean displaySummary;
	
	private final List<String> excludes;
	
	public StandardOptions(String[] args) throws ParseException {
		this.cl = new GnuParser().parse(OPTIONS, args);
		this.displayHelp = cl.hasOption(HELP_OPT);
		this.displaySummary = cl.hasOption(SUMMARY_OPT);
		this.excludes = getOptionList(EXCLUDE_OPT);
	}
	
	private List<String> getOptionList(String opt) {
		String listString = cl.getOptionValue(opt);
		if (listString != null) {
			return Arrays.asList(StringUtils.split(listString, ','));
		} else {
			return Collections.EMPTY_LIST;
		}
	}

	public boolean isDisplaySummary() {
		return displaySummary;
	}
	
	public boolean isDisplayHelp() {
		return displayHelp;
	}
	
	public List<String> getExcludes() {
		return excludes;
	}
	
	public void printHelp() {
		HelpFormatter helpFormatter = new HelpFormatter();
		helpFormatter.setWidth(MAX_WIDTH);
		helpFormatter.printHelp(COMMAND_SYNTAX, "", OPTIONS, "");
	}
}