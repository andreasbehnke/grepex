package org.grepex;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.junit.Test;

public class TestExceptionParser {

	@Test
	public void testParse() throws IOException {
		Reader input = new StringReader("fadsds\n"
				+ "sadfdsfdsfdsf\n"
				+ "sadfdsafdsfdsf\n"
				+ "asfdsfsafsf\n"
				+ "asdfdsfdsafds\n"
				+ "sadfdsfdsf\n"
				+ "this.is.an.Exception\n"
				+ "      abc\n"
				+ "      def\n"
				+ "      ghi\n"
				+ "Caused by: another.Exception\n"
				+ "      jkl\n"
				+ "      mno\n"
				+ "afdsfsdafdsfdsafds\n"
				+ "sadfdsafdsfdsafdsaf\n"
				+ "dsafdsafdsafdsafds\n"
				+ "sadfsda\n"
				+ "sadfdsafdsafdsaf\n"
				+ "sadfsaf\n"
				+ "safddsafdsaf\n"
				+ "afdsfs\n"
				+ "this.is.the.second.Exception\n"
				+ "      pqr\n"
				+ "      stu\n"
				+ "      vwx\n"
				+ "Caused by: the.third.Exception\n"
				+ "      yz\n"
				+ "      abc\n"
				+ "gsdfg\n"
				+ "sdfgdfg\n");
		ExceptionParser parser = new ExceptionParser(input, 5);
		Stacktrace stack = parser.next();
		assertNotNull(parser.next());
		assertNull(parser.next());
		assertNotNull(stack);
		assertEquals(5, stack.getContext().size());
		assertEquals("sadfdsfdsfdsf", stack.getContext().get(0));
		assertEquals("sadfdsfdsf", stack.getContext().get(4));
		
	}
}
