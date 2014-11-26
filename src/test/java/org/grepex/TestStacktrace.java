package org.grepex;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestStacktrace {

	@Test
	public void testAddLine() {
		Stacktrace stacktrace = new Stacktrace();
		stacktrace.addLine("top.level.exception.Exception: test");
		stacktrace.addLine("\tthis.is.a.stack.trace.line");
		stacktrace.addLine("   this.is.another.line");
		stacktrace.addLine("root.cause.of.exception.Exception");
		stacktrace.addLine("  just.another.line");
		
		assertEquals(5, stacktrace.getLines().size());
		assertEquals(2, stacktrace.getCauses().size());
		assertEquals("top.level.exception.Exception: test", stacktrace.getLines().get(0));
		assertEquals("top.level.exception.Exception: test", stacktrace.getCauses().get(0));
		assertEquals("root.cause.of.exception.Exception", stacktrace.getLines().get(3));
		assertEquals("root.cause.of.exception.Exception", stacktrace.getCauses().get(1));
		assertEquals("  just.another.line", stacktrace.getLines().get(4));
	}
}
