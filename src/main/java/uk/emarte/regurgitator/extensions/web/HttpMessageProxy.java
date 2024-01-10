/*
 * Copyright (C) 2017 Miles Talmey.
 * Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
 */
package uk.emarte.regurgitator.extensions.web;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import uk.emarte.regurgitator.core.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static java.lang.String.valueOf;
import static uk.emarte.regurgitator.core.CoreTypes.STRING;
import static uk.emarte.regurgitator.core.Log.getLog;
import static uk.emarte.regurgitator.core.StringType.stringify;
import static uk.emarte.regurgitator.extensions.web.CookieUtil.cookieToString;
import static uk.emarte.regurgitator.extensions.web.CookieUtil.stringToCookie;
import static uk.emarte.regurgitator.extensions.web.ExtensionsWebConfigConstants.*;

class HttpMessageProxy {
    private static final Log log = getLog(HttpMessageProxy.class);
    private final HttpClientWrapper clientWrapper;

    HttpMessageProxy(HttpClientWrapper clientWrapper) {
        this.clientWrapper = clientWrapper;
    }

    Message proxyMessage(Message message) throws RegurgitatorException {
        long start = System.currentTimeMillis();
        String username = clientWrapper.getUsername();
        log.debug("Proxying message to '{}:{}'" + (username != null ? " with credentials for '" + username + "'": ""), clientWrapper.getHost(), clientWrapper.getPort());
        HttpMethod method = getMethod(message);
        setPath(method, message);
        setQueryString(method, message);
        addHeaders(message.getContext(REQUEST_HEADERS_CONTEXT), method);

        try {
            log.debug("Executing method");
            int status = clientWrapper.executeMethod(method, getCookies(message.getContext(REQUEST_COOKIES_CONTEXT)));
            log.debug("Creating new message");
            Message newMessage = new Message(message, true, true);
            setStatusCode(status, newMessage);
            setPayload(method, newMessage);
            addHeaders(method, newMessage);
            addCookies(clientWrapper, newMessage);
            method.releaseConnection();
            return newMessage;
        } catch (IOException e) {
            throw new RegurgitatorException("Exception making http call", e);
        } finally {
            long end = System.currentTimeMillis();
            log.debug("All done: {} milliseconds", end - start);
        }
    }

    private HttpMethod getMethod(Message message) throws RegurgitatorException {
        Parameter methodParameter = message.getContextValue(new ContextLocation(REQUEST_METADATA_CONTEXT, METHOD));
        String method = methodParameter != null ? stringify(methodParameter.getValue()) : GET;

        if(GET.equals(method)) {
            log.debug("Using get method");
            return clientWrapper.newGetMethod();
        }

        if(POST.equals(method)) {
            log.debug("Using post method");
            PostMethod postMethod = clientWrapper.newPostMethod();
            Object payload = message.getContext(REQUEST_PAYLOAD_CONTEXT).getValue(TEXT);

            if(payload != null) {
                String requestBody = stringify(payload);
                log.debug("Adding payload to post method: '{}'", requestBody);
                try {
                    Parameters context = message.getContext(REQUEST_METADATA_CONTEXT);
                    postMethod.setRequestEntity(new StringRequestEntity(requestBody, stringify(context.getValue(CONTENT_TYPE)), stringify(context.getValue(CHARACTER_ENCODING))));
                } catch(UnsupportedEncodingException uee) {
                    throw new RegurgitatorException("Error encoding post body", uee);
                }
            }

            return postMethod;
        }

        if(PUT.equals(method)) {
            log.debug("Using put method");
            PutMethod putMethod = clientWrapper.newPutMethod();
            Object payload = message.getContext(REQUEST_PAYLOAD_CONTEXT).getValue(TEXT);

            if(payload != null) {
                String requestBody = stringify(payload);
                log.debug("Adding payload to put method: '{}'", requestBody);
                try {
                    Parameters context = message.getContext(REQUEST_METADATA_CONTEXT);
                    putMethod.setRequestEntity(new StringRequestEntity(requestBody, stringify(context.getValue(CONTENT_TYPE)), stringify(context.getValue(CHARACTER_ENCODING))));
                } catch(UnsupportedEncodingException uee) {
                    throw new RegurgitatorException("Error encoding put body", uee);
                }
            }
            return putMethod;
        }

        if(DELETE.equals(method)) {
            log.debug("Using delete method");
            return clientWrapper.newDeleteMethod();
        }

        throw new RegurgitatorException("Invalid method in request metadata: " + method);
    }

    private static void setPath(HttpMethod method, Message message) {
        String path = stringify(message.getContextValue(new ContextLocation(REQUEST_METADATA_CONTEXT, PATH_INFO)));

        if(path != null) {
            log.debug("Setting method path to '{}'", path);
            method.setPath(path);
        }
    }

    private static void setQueryString(HttpMethod method, Message message) {
        String queryString = stringify(message.getContextValue(new ContextLocation(REQUEST_METADATA_CONTEXT, QUERY_STRING)));

        if(queryString != null) {
            log.debug("Setting method query string to '{}'", queryString);
            method.setQueryString(queryString);
        }
    }

    private static void addHeaders(Parameters context, HttpMethod method) {
        for(Object id: context.ids()) {
            String value = stringify(context.getValue(id));
            log.debug("Adding request header '{}' with value '{}' to method", id, value);
            method.addRequestHeader(stringify(id), value);
        }
    }

    private Cookie[] getCookies(Parameters context) throws RegurgitatorException {
        List<Object> ids = context.ids();
        Cookie[] cookies = new Cookie[ids.size()];

        for(int i = 0; i < ids.size(); i++) {
            Object id = ids.get(i);
            Object value = context.getValue(id);
            log.debug("Adding request cookie '{}' with value '{}' to method", id, value);
            cookies[i] = stringToCookie(stringify(value));
        }

        return cookies;
    }

    private static void setStatusCode(int status, Message message) {
        log.debug("Setting response status code to '{}'", status);
        message.getContext(RESPONSE_METADATA_CONTEXT).setValue(STATUS_CODE, STRING, valueOf(status));
    }

    private static void setPayload(HttpMethod method, Message message) throws RegurgitatorException {
        try {
            byte[] responseBody = method.getResponseBody();

            if(responseBody != null && responseBody.length > 0) {
                String responsePayload = new String(responseBody);
                log.debug("Adding response payload");
                message.getContext(RESPONSE_PAYLOAD_CONTEXT).setValue(TEXT, STRING, responsePayload);
            } else {
                log.debug("No response payload");
            }
        } catch (IOException re) {
            throw new RegurgitatorException("IO error getting response body", re);
        }
    }

    private static void addHeaders(HttpMethod method, Message message) {
        Header[] responseHeaders = method.getResponseHeaders();

        if(responseHeaders != null && responseHeaders.length > 0) {
            log.debug("Adding response headers");
            Parameters context = message.getContext(RESPONSE_HEADERS_CONTEXT);

            for(Header header : responseHeaders) {
                context.setValue(header.getName(), STRING, header.getValue());
            }
        }
    }

    private static void addCookies(HttpClientWrapper wrapper, Message message) {
        List<Cookie> lastCookies = wrapper.getLastCookies();

        if(lastCookies != null && lastCookies.size() > 0) {
            log.debug("Adding response cookies");
            Parameters context = message.getContext(RESPONSE_COOKIES_CONTEXT);

            for(Cookie cookie: lastCookies) {
                context.setValue(cookie.getName(), STRING, cookieToString(cookie));
            }
        }
    }
}
