/*
 * Copyright (C) 2017 Miles Talmey.
 * Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
 */
package uk.emarte.regurgitator.extensions.web;

import org.junit.Test;
import uk.emarte.regurgitator.core.Message;
import uk.emarte.regurgitator.core.RegurgitatorException;
import uk.emarte.regurgitator.core.ValueProcessor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class QueryParamProcessorTest {
    private final ValueProcessor toTest = new QueryParamProcessor("key");

    @Test
    public void testProcessor() throws RegurgitatorException {
        assertEquals("this", toTest.process("key=this", null));
        assertEquals("this", toTest.process("key=this&otherKey=that", null));
        assertEquals("this,somethingElse,anotherThing", toTest.process("key=this&otherKey=that&key=somethingElse&thirdKey=whatever&key=anotherThing", null));
        assertNull(toTest.process("notKey=this&otherKey=that&notKey=somethingElse&thirdKey=whatever&notKey=anotherThing", null));
    }

    @Test(expected = RegurgitatorException.class)
    public void testNotAQueryString() throws RegurgitatorException {
        toTest.process("this is not a query string", new Message(null));
    }

    @Test
    public void testPassThrough() throws RegurgitatorException {
        assertNull(toTest.process(null, new Message(null)));
    }
}
