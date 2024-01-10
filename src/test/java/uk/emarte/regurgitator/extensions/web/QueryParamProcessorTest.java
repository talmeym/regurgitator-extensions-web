/*
 * Copyright (C) 2017 Miles Talmey.
 * Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
 */
package uk.emarte.regurgitator.extensions.web;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class QueryParamProcessorTest {
    private final QueryParamProcessor toTest = new QueryParamProcessor("key");

    @Test
    public void testProcess() {
        assertEquals("this", toTest.process("key=this&otherKey=that", null));
        assertEquals("this,somethingElse,anotherThing", toTest.process("key=this&otherKey=that&key=somethingElse&thirdKey=whatever&key=anotherThing", null));
        assertNull(toTest.process("notKey=this&otherKey=that&notKey=somethingElse&thirdKey=whatever&notKey=anotherThing", null));
    }
}
