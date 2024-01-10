/*
 * Copyright (C) 2017 Miles Talmey.
 * Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
 */
package uk.emarte.regurgitator.extensions.web;

import uk.emarte.regurgitator.core.Log;
import uk.emarte.regurgitator.core.Message;
import uk.emarte.regurgitator.core.Parameters;
import uk.emarte.regurgitator.core.RegurgitatorException;

import javax.servlet.http.HttpServletResponse;

import static uk.emarte.regurgitator.core.Log.getLog;
import static uk.emarte.regurgitator.core.StringType.stringify;
import static uk.emarte.regurgitator.extensions.web.CookieUtil.stringToHttpCookie;
import static uk.emarte.regurgitator.extensions.web.ExtensionsWebConfigConstants.*;

class HttpResponseUtil {
    private static final Log log = getLog(HttpResponseUtil.class);

    static void applyResponseData(Message message, HttpServletResponse httpServletResponse) {
        addResponseHeaders(message, httpServletResponse);
        addResponseMetadata(message, httpServletResponse);
        addResponseCookies(message, httpServletResponse);
    }

    private static void addResponseHeaders(Message message, HttpServletResponse response) {
        Parameters context = message.getContext(RESPONSE_HEADERS_CONTEXT);

        if(context.size() > 0) {
            log.debug("Adding headers to http response from message");

            for (Object id : context.ids()) {
                Object value = context.getValue(id);
                log.debug("Setting header '{}' to value '{}'", id, value);
                response.addHeader(stringify(id), stringify(value));
            }
        }
    }

    private static void addResponseMetadata(Message message, HttpServletResponse httpServletResponse) {
        Parameters context = message.getContext(RESPONSE_METADATA_CONTEXT);

        if(context.size() > 0) {
            log.debug("Adding metadata to http response from message");

            if (context.contains(STATUS_CODE)) {
                Object value = context.getValue(STATUS_CODE);
                log.debug("Setting status code '{}'", value);
                httpServletResponse.setStatus(objToInt(value));
            }

            if (context.contains(CONTENT_TYPE)) {
                Object value = context.getValue(CONTENT_TYPE);
                log.debug("Setting content type to '{}'", value);
                httpServletResponse.setContentType(value.toString());
            }

            if (context.contains(CHARACTER_ENCODING)) {
                Object value = context.getValue(CHARACTER_ENCODING);
                log.debug("Setting character encoding to '{}'", value);
                httpServletResponse.setCharacterEncoding(value.toString());
            }

            if (context.contains(CONTENT_LENGTH)) {
                Object value = context.getValue(CONTENT_LENGTH);
                log.debug("Setting content length to '{}'", value);
                httpServletResponse.setContentLength(objToInt(value));
            }
        }
    }

    private static void addResponseCookies(Message message, HttpServletResponse httpServletResponse) {
        Parameters context = message.getContext(RESPONSE_COOKIES_CONTEXT);

        if(context.size() > 0) {
            log.debug("Adding cookies to http response from message");

            for (Object id : context.ids()) {
                try {
                    Object value = context.getValue(id);
                    log.debug("Adding cookie '{}' with value '{}'", id, value);
                    httpServletResponse.addCookie(stringToHttpCookie(stringify(value)));
                } catch (RegurgitatorException e) {
                    log.error("Error parsing cookie for http response", e);
                }

            }
        }
    }

    private static int objToInt(Object value) {
        return Integer.parseInt(String.valueOf(value));
    }
}
