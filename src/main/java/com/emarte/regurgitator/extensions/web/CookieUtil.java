/*
 * Copyright (C) 2017 Miles Talmey.
 * Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
 */
package com.emarte.regurgitator.extensions.web;

import com.emarte.regurgitator.core.RegurgitatorException;
import org.apache.commons.httpclient.Cookie;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.emarte.regurgitator.core.StringType.stringify;
import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;

class CookieUtil {
    private static final String PATTERN = "domain={0};name={1};value={2};path={3};expiryDate={4};secure={5};comment={6};version={7};";
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final int DEFAULT_VERSION = 0;

    static String httpCookieToString(javax.servlet.http.Cookie cookie) {
        return cookieToString(httpCookieToCookie(cookie));
    }

    private static Cookie httpCookieToCookie(javax.servlet.http.Cookie httpCookie) {
        return new Cookie(httpCookie.getDomain(), httpCookie.getName(), httpCookie.getValue(), httpCookie.getPath(), httpCookie.getMaxAge(), httpCookie.getSecure());
    }

    static String cookieToString(Cookie cookie) {
        String name = cookie.getName();
        String value = cookie.getValue();
        String domain = cookie.getDomain();
        String comment = cookie.getComment();
        Date expiryDate = cookie.getExpiryDate();
        String path = cookie.getPath();
        boolean secure = cookie.getSecure();
        int version = cookie.getVersion();
        String date = expiryDate != null ? new SimpleDateFormat(DATE_FORMAT).format(expiryDate) : null;
        return new MessageFormat(PATTERN).format(new Object[]{domain, name, value, path, date, secure, comment, version});
    }

    static Cookie stringToCookie(String string) throws RegurgitatorException {
        try {
            Object[] objects = new MessageFormat(PATTERN).parse(string);
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
            final Cookie cookie =
                    new Cookie(
                            nullStringSafe(stringify(objects[0])),
                            stringify(objects[1]),
                            stringify(objects[2]),
                            nullStringSafe(stringify(objects[3])),
                            nullStringSafe(stringify(objects[4])) != null ? dateFormat.parse(stringify(objects[4])) : null,
                            parseBoolean(stringify(objects[5]))
                    );

            if (objects[3] != null && !"null".equals(objects[6])) {
                cookie.setComment(stringify(objects[6]));
            }

            if (objects[7] != null && !Integer.valueOf(DEFAULT_VERSION).equals(objects[7])) {
                cookie.setVersion(parseInt(stringify(objects[7])));
            }

            return cookie;
        } catch (ParseException e) {
            throw new RegurgitatorException("boom");
        }
    }

    static javax.servlet.http.Cookie stringToHttpCookie(String string) throws RegurgitatorException {
        return cookieToHttpCookie(stringToCookie(string));
    }

    private static javax.servlet.http.Cookie cookieToHttpCookie(Cookie cookie) {
        javax.servlet.http.Cookie httpCookie = new javax.servlet.http.Cookie(cookie.getName(), cookie.getValue());

        if(cookie.getDomain() != null) {
            httpCookie.setDomain(cookie.getDomain());
        }

        if(cookie.getPath() != null) {
            httpCookie.setPath(cookie.getPath());
        }

        if(cookie.getExpiryDate() != null) {
            httpCookie.setMaxAge((int) ((cookie.getExpiryDate().getTime() - System.currentTimeMillis()) / 1000L));
        }

        if(cookie.getSecure()) {
            httpCookie.setSecure(cookie.getSecure());
        }

        if(cookie.getComment() != null) {
            httpCookie.setComment(cookie.getComment());
        }

        if(cookie.getVersion() != DEFAULT_VERSION) {
            httpCookie.setVersion(cookie.getVersion());
        }

        return httpCookie;
    }

    private static String nullStringSafe(String string) {
        if (!"null".equals(string)) {
            return string;
        }

        return null;
    }
}
