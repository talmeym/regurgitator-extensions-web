package com.emarte.regurgitator.extensions.web;

import com.emarte.regurgitator.core.*;

import javax.servlet.http.*;
import java.io.*;
import java.util.Enumeration;

import static com.emarte.regurgitator.core.CoreTypes.*;
import static com.emarte.regurgitator.extensions.web.ExtensionsWebConfigConstants.*;

public class HttpRequestUtil {
    private static final Log log = Log.getLog(HttpRequestUtil.class);

    public static Message applyRequestData(Message message, HttpServletRequest httpServletRequest) throws RegurgitatorException, IOException {
        addRequestHeaders(message, httpServletRequest);
        addRequestMetadata(message, httpServletRequest);
        addRequestCookies(message, httpServletRequest);
        addPayload(message, httpServletRequest);
        return message;
    }

    private static void addRequestMetadata(Message message, HttpServletRequest request) throws RegurgitatorException {
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

    private static void addPayload(Message message, HttpServletRequest request) throws IOException, RegurgitatorException {
        log.debug("Adding payload to message from http request");
        addStringParam(message, REQUEST_PAYLOAD_CONTEXT, TEXT, getPayload(request));
    }

    private static void addRequestHeaders(Message message, HttpServletRequest request) throws RegurgitatorException {
        log.debug("Adding headers to message from http request");
        Enumeration<String> headerNames = request.getHeaderNames();

        while(headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            String value = request.getHeader(name);
            addStringParam(message, REQUEST_HEADERS_CONTEXT, name, value);
        }
    }

    private static void addRequestCookies(Message message, HttpServletRequest request) throws RegurgitatorException {
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

    private static void addStringParam(Message message, String context, String name, String value) throws RegurgitatorException {
        if(value != null && value.length() > 0) {
            message.getContext(context).setValue(name, STRING, value);
        }
    }

    private static void addIntegerParam(Message message, String context, String name, Integer value) throws RegurgitatorException {
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
