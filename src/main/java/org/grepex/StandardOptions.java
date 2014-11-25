package org.grepex;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class StandardOptions {
	
	public static final String SEPARATOR = red("------");
	
	public static final int MAX_WIDTH = 120;
	
	private static final String COMMAND_SYNTAX = "grepex [-s|--summary] [-h|--help]" + System.lineSeparator();
	
	private static final String HELP_OPT = "h";
	
	private static final Option HELP = new Option(HELP_OPT, "help", false, "Print this help message");
	
	private static final String SUMMARY_OPT = "s";
	
	private static final Option SUMMARY = new Option(SUMMARY_OPT, "summary", false, "Print all unique exceptions after processing input ordered by number of occurrences.");

	private static final Options OPTIONS = new Options().addOption(HELP).addOption(SUMMARY);
	
	private final CommandLine cl;
	
	public StandardOptions(String[] args) throws ParseException {
		this.cl = new GnuParser().parse(OPTIONS, args);
	}

	public boolean isDisplaySummary() {
		return cl.hasOption(SUMMARY_OPT);
	}
	
	public boolean isDisplayHelp() {
		return cl.hasOption(HELP_OPT);
	}
	
	public void printHelp() {
		HelpFormatter helpFormatter = new HelpFormatter();
		helpFormatter.setWidth(MAX_WIDTH);
		helpFormatter.printHelp(COMMAND_SYNTAX, "", OPTIONS, "");
	}
	
	public static String red(String text) {
		return (char)27 + "[31m" + text + (char)27 + "[0m";
	}
}