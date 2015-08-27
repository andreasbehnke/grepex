package org.grepex;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class TestExceptionParser {

	public static final String LOG_INPUT = "fadsds\n"
			+ "sadfdsfdsfdsf\n"
			+ "sadfdsafdsfdsf\n"
			+ "asfdsfsafsf\n"
			+ "asdfdsfdsafds\n"
			+ "sadfdsfdsf\n"
			+ "this.is.an.Exception\n"
			+ "      at abc\n"
			+ "      at def\n"
			+ "      at ghi\n"
			+ "Caused by: another.Exception\n"
			+ "      at jkl\n"
			+ "      at mno\n"
			+ "afdsfsdafdsfdsafds\n"
			+ "sadfdsafdsfdsafdsaf\n"
			+ "dsafdsafdsafdsafds\n"
			+ "sadfsda\n"
			+ "sadfdsafdsafdsaf\n"
			+ "sadfsaf\n"
			+ "safddsafdsaf\n"
			+ "afdsfs\n"
			+ "this.is.the.second.Exception\n"
			+ "      at pqr\n"
			+ "      at stu\n"
			+ "      at vwx\n"
			+ "Caused by: the.third.Exception\n"
			+ "      at yz\n"
			+ "      at abc\n"
			+ "gsdfg\n"
			+ "sdfgdfg\n";

	@Test
	public void testParse() throws IOException {
		Reader input = new StringReader(LOG_INPUT);
		ExceptionParser parser = new ExceptionParser(input, 5, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
		Stacktrace stack1 = parser.next();
		Stacktrace stack2 = parser.next();
		assertNull(parser.next());
		
		assertNotNull(stack1);
		assertEquals(5, stack1.getContext().size());
		assertEquals(6, stack1.getLineNumber());
		assertEquals("sadfdsfdsfdsf", stack1.getContext().get(0));
		assertEquals("sadfdsfdsf", stack1.getContext().get(4));
		
		assertNotNull(stack2);
		assertEquals(5, stack2.getContext().size());
		assertEquals(21, stack2.getLineNumber());
		assertEquals("sadfsda", stack2.getContext().get(0));
		assertEquals("afdsfs", stack2.getContext().get(4));	
	}

	@Test
	public void testInclude() throws IOException {
		Reader input = new StringReader(LOG_INPUT);
		List<String> includes = Arrays.asList("the.third.Exception");
		ExceptionParser parser = new ExceptionParser(input, 5, includes, Collections.EMPTY_LIST);
		assertEquals("this.is.the.second.Exception", parser.next().getCauses().get(0));
		assertNull(parser.next());
	}

	@Test
	public void testExclude() throws IOException {
		Reader input = new StringReader(LOG_INPUT);
		List<String> excludes = Arrays.asList("the.third.Exception");
		ExceptionParser parser = new ExceptionParser(input, 5, Collections.EMPTY_LIST, excludes);
		assertEquals("this.is.an.Exception", parser.next().getCauses().get(0));
		assertNull(parser.next());
	}

	@Test
	public void testIncludeAndExclude() throws IOException {
		Reader input = new StringReader(LOG_INPUT);
		List<String> excludes = Arrays.asList("the.third.Exception");
		List<String> includes = Arrays.asList("this.is.the.second.Exception");
		ExceptionParser parser = new ExceptionParser(input, 5, includes, excludes);
		assertNull(parser.next());

		input = new StringReader(LOG_INPUT);
		excludes = Arrays.asList("the.third.Exception");
		includes = Arrays.asList("another.Exception");
		parser = new ExceptionParser(input, 5, includes, excludes);
		assertEquals("this.is.an.Exception", parser.next().getCauses().get(0));
		assertNull(parser.next());
	}
}
