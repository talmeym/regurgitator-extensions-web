/*
 * Copyright (C) 2017 Miles Talmey.
 * Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
 */
package com.emarte.regurgitator.extensions.web;

import com.emarte.regurgitator.core.RegurgitatorException;
import org.apache.commons.httpclient.Cookie;
import org.junit.Test;

import static com.emarte.regurgitator.extensions.web.CookieUtil.cookieToString;
import static com.emarte.regurgitator.extensions.web.CookieUtil.stringToCookie;
import static org.junit.Assert.*;

public class CookieUtilTest {

    private static final String NAME = "name";
    private static final String VALUE = "value";
    private static final String DOMAIN = "domain";
    private static final String COMMENT = "comment";
    private static final int MAX_AGE = 42;
    private static final String PATH = "/this/that";
    private static final boolean SECURE = true;
    private static final int VERSION = 24;

    @Test
    public void testEverything() throws RegurgitatorException {
        Cookie cookie = new Cookie(DOMAIN, NAME, VALUE, PATH, MAX_AGE, SECURE);
        cookie.setComment(COMMENT);
        cookie.setVersion(VERSION);

        Cookie result = stringToCookie(cookieToString(cookie));

        assertEquals(cookie.getName(), result.getName());
        assertEquals(cookie.getValue(), result.getValue());
        assertEquals(cookie.getDomain(), result.getDomain());
        assertEquals(cookie.getComment(), result.getComment());
        assertEquals(cookie.getExpiryDate().getTime(), result.getExpiryDate().getTime());
        assertEquals(cookie.getPath(), result.getPath());
        assertEquals(cookie.getSecure(), result.getSecure());
        assertEquals(cookie.getVersion(), result.getVersion());
    }

    @Test
    public void testNameValue() throws RegurgitatorException {
        Cookie cookie = new Cookie(null, NAME, VALUE);

        Cookie result = stringToCookie(cookieToString(cookie));

        assertEquals(cookie.getName(), result.getName());
        assertEquals(cookie.getValue(), result.getValue());
        assertNull(result.getDomain());
        assertNull(result.getComment());
        assertNull(result.getExpiryDate());
        assertNull(result.getPath());
        assertFalse(result.getSecure());
        assertEquals(0, result.getVersion());
    }

    @Test
    public void testMixture() throws RegurgitatorException {
        Cookie cookie = new Cookie(DOMAIN, NAME, VALUE, PATH, MAX_AGE, false);

        Cookie result = stringToCookie(cookieToString(cookie));

        assertEquals(cookie.getName(), result.getName());
        assertEquals(cookie.getValue(), result.getValue());
        assertEquals(cookie.getDomain(), result.getDomain());
        assertNull(result.getComment());
        assertEquals(cookie.getExpiryDate(), result.getExpiryDate());
        assertEquals(cookie.getPath(), result.getPath());
        assertFalse(result.getSecure());
        assertEquals(0, result.getVersion());
    }
}