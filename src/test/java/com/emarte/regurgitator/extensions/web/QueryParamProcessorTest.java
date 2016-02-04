package com.emarte.regurgitator.extensions.web;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class QueryParamProcessorTest {
	private final QueryParamProcessor toTest = new QueryParamProcessor("key");

	@Test
	public void testProcess() throws Exception {
		assertEquals("this", toTest.process("key=this&otherKey=that", null));
		assertEquals("this,somethingElse,anotherThing", toTest.process("key=this&otherKey=that&key=somethingElse&thirdKey=whatever&key=anotherThing", null));
		assertNull(toTest.process("notKey=this&otherKey=that&notKey=somethingElse&thirdKey=whatever&notKey=anotherThing", null));
	}
}