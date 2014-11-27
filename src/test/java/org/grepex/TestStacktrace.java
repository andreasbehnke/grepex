package org.grepex;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;

public class TestStacktrace {

	@Test
	public void testAddLine() {
		Stacktrace stacktrace = new Stacktrace(null, "Exception", -1);
		assertFalse(stacktrace.addLine("top.level.exception.Exception: test"));
		assertTrue(stacktrace.addLine("\tat this.is.a.stack.trace.line"));
		assertTrue(stacktrace.addLine("   at this.is.another.line"));
		assertTrue(stacktrace.addLine("Caused by: root.cause.of.exception.Exception"));
		assertTrue(stacktrace.addLine("  at just.another.line"));
		
		assertEquals(5, stacktrace.getLines().size());
		assertEquals(2, stacktrace.getCauses().size());
		assertEquals("Exception", stacktrace.getLines().get(0));
		assertEquals("Exception", stacktrace.getCauses().get(0));
		assertEquals("\tat this.is.a.stack.trace.line", stacktrace.getLines().get(1));
		assertEquals("Caused by: root.cause.of.exception.Exception", stacktrace.getCauses().get(1));
		assertEquals("Caused by: root.cause.of.exception.Exception", stacktrace.getLines().get(3));
	}
	
	@Test
	public void testFindStacktraceStart() {
		Collection<String> context = new ArrayList<>();
		context.add("Log line 1");
		context.add("Log line 2");
		context.add("Log line 3");
		context.add("this.is.an.Exception");
		String lastLine = "\tat first.trace.line";
		context.add(lastLine);
		Stacktrace stacktrace = Stacktrace.findStacktraceStart(lastLine, 4, context);
		
		assertNotNull(stacktrace);
		assertEquals(3, stacktrace.getContext().size());
		assertEquals("Log line 1", stacktrace.getContext().get(0));
		assertEquals("Log line 2", stacktrace.getContext().get(1));
		assertEquals("Log line 3", stacktrace.getContext().get(2));
		assertEquals(1, stacktrace.getCauses().size());
		assertEquals("this.is.an.Exception", stacktrace.getCauses().get(0));
		assertEquals(2, stacktrace.getLines().size());
		assertEquals("this.is.an.Exception", stacktrace.getLines().get(0));
		assertEquals("\tat first.trace.line", stacktrace.getLines().get(1));
	}
	
	@Test
	public void testFindStacktraceStartNotFound() {
		Collection<String> context = new ArrayList<>();
		context.add("Log line 1");
		context.add("Log line 2");
		context.add("Log line 3");
		context.add("this.is.NOT");
		String lastLine = "\tat first.trace.line";
		context.add(lastLine);
		Stacktrace stacktrace = Stacktrace.findStacktraceStart(lastLine, 4, context);
		
		assertNull(stacktrace);
		
		context = new ArrayList<>();
		context.add("Log line 1");
		context.add("Log line 2");
		context.add("Log line 3");
		context.add("this.is.NOT");
		stacktrace = Stacktrace.findStacktraceStart(lastLine, 4, context);
		
		assertNull(stacktrace);
	}
}
