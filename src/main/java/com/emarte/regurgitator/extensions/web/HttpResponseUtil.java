/*
 * Copyright (C) 2017 Miles Talmey.
 * Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
 */
package com.emarte.regurgitator.extensions.web;

import com.emarte.regurgitator.core.*;

import javax.servlet.http.HttpServletResponse;

import static com.emarte.regurgitator.core.Log.getLog;
import static com.emarte.regurgitator.extensions.web.ExtensionsWebConfigConstants.*;

class HttpResponseUtil {
    private static final Log log = getLog(HttpResponseUtil.class);

    static void applyResponseData(Message message, HttpServletResponse httpServletResponse) {
        addResponseHeaders(message, httpServletResponse);
        addResponseMetadata(message, httpServletResponse);
    }

    private static void addResponseHeaders(Message message, HttpServletResponse response) {
        Parameters context = message.getContext(RESPONSE_HEADERS_CONTEXT);

        if(context.size() > 0) {
            log.debug("Adding headers to http response from message");

            for (Object id : context.ids()) {
                Object value = context.getValue(id);
                log.debug("Setting header '{}' to value '{}'", id, value);
                response.addHeader(String.valueOf(id), value.toString());
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

    private static int objToInt(Object value) {
        return Integer.parseInt(String.valueOf(value));
    }
}
