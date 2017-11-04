/*
 * Copyright (C) 2017 Miles Talmey.
 * Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
 */
package com.emarte.regurgitator.extensions.web;

import com.emarte.regurgitator.core.Log;
import com.emarte.regurgitator.core.Message;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.Enumeration;

import static com.emarte.regurgitator.core.CoreTypes.NUMBER;
import static com.emarte.regurgitator.core.CoreTypes.STRING;
import static com.emarte.regurgitator.core.Log.getLog;
import static com.emarte.regurgitator.extensions.web.ExtensionsWebConfigConstants.*;

class HttpRequestUtil {
    private static final Log log = getLog(HttpRequestUtil.class);

    static void applyRequestData(HttpServletRequest httpServletRequest, Message message) throws IOException {
        addRequestHeaders(httpServletRequest, message);
        addRequestMetadata(httpServletRequest, message);
        addRequestCookies(message, httpServletRequest);
        addPayload(httpServletRequest, message);
    }

    private static void addRequestMetadata(HttpServletRequest request, Message message) {
        log.debug("Adding metadata to message from http request");
        addStringParam(message, REQUEST_METADATA_CONTEXT, METHOD, request.getMethod());
        addStringParam(message, REQUEST_METADATA_CONTEXT, REQUEST_URI, request.getRequestURI());
        addStringParam(message, REQUEST_METADATA_CONTEXT, QUERY_STRING, request.getQueryString());
        addStringParam(message, REQUEST_METADATA_CONTEXT, AUTH_TYPE, request.getAuthType());
        addStringParam(message, REQUEST_METADATA_CONTEXT, CONTEXT_PATH, request.getContextPath());
        addStringParam(message, REQUEST_METADATA_CONTEXT, PATH_INFO, request.getPathInfo());
        addStringParam(message, REQUEST_METADATA_CONTEXT, PATH_TRANSLATED, request.getPathTranslated());
        addStringParam(message, REQUEST_METADATA_CONTEXT, REMOTE_USER, request.getRemoteUser());
        addStringParam(message, REQUEST_METADATA_CONTEXT, REQUESTED_SESSION_ID, request.getRequestedSessionId());
        addStringParam(message, REQUEST_METADATA_CONTEXT, SERVLET_PATH, request.getServletPath());
        addStringParam(message, REQUEST_METADATA_CONTEXT, CHARACTER_ENCODING, request.getCharacterEncoding());
        addStringParam(message, REQUEST_METADATA_CONTEXT, CONTENT_TYPE, request.getContentType());
        addStringParam(message, REQUEST_METADATA_CONTEXT, LOCAL_ADDRESS, request.getLocalAddr());
        addStringParam(message, REQUEST_METADATA_CONTEXT, LOCAL_NAME, request.getLocalName());
        addStringParam(message, REQUEST_METADATA_CONTEXT, PROTOCOL, request.getProtocol());
        addStringParam(message, REQUEST_METADATA_CONTEXT, REMOTE_ADDRESS, request.getRemoteAddr());
        addStringParam(message, REQUEST_METADATA_CONTEXT, REMOTE_HOST, request.getRemoteHost());
        addStringParam(message, REQUEST_METADATA_CONTEXT, SCHEME, request.getScheme());
        addStringParam(message, REQUEST_METADATA_CONTEXT, SERVER_NAME, request.getServerName());
        addIntegerParam(message, REQUEST_METADATA_CONTEXT, CONTENT_LENGTH, request.getContentLength());
        addIntegerParam(message, REQUEST_METADATA_CONTEXT, LOCAL_PORT, request.getLocalPort());
        addIntegerParam(message, REQUEST_METADATA_CONTEXT, SERVER_PORT, request.getServerPort());
        addStringParam(message, REQUEST_METADATA_CONTEXT, HTTP_SESSION_ID, request.getSession(true).getId());
    }

    private static void addPayload(HttpServletRequest request, Message message) throws IOException {
        log.debug("Adding payload to message from http request");
        addStringParam(message, REQUEST_PAYLOAD_CONTEXT, TEXT, getPayload(request));
    }

    private static void addRequestHeaders(HttpServletRequest request, Message message) {
        Enumeration<String> headerNames = request.getHeaderNames();

        if(headerNames.hasMoreElements()) {
            log.debug("Adding headers to message from http request");

            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                String value = request.getHeader(name);
                addStringParam(message, REQUEST_HEADERS_CONTEXT, name, value);
            }
        }
    }

    private static void addRequestCookies(Message message, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if(cookies != null) {
            log.debug("Adding cookies to message from http request");

            for(Cookie cookie: cookies) {
                String name = cookie.getName();
                String value = cookie.getValue();
                addStringParam(message, REQUEST_COOKIES_CONTEXT, name, value);
            }
        }
    }

    private static void addStringParam(Message message, String context, String name, String value) {
        if(value != null && value.length() > 0) {
            message.getContext(context).setValue(name, STRING, value);
        }
    }

    private static void addIntegerParam(Message message, String context, String name, Integer value) {
        if(value != null) {
            message.getContext(context).setValue(name, NUMBER, (long) value);
        }
    }

    private static String getPayload(HttpServletRequest request) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        InputStream input = request.getInputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;

        while((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }

        return new String(output.toByteArray());
    }
}
