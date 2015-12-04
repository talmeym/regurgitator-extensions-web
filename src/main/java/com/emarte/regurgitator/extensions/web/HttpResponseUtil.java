package com.emarte.regurgitator.extensions.web;

import com.emarte.regurgitator.core.*;

import javax.servlet.http.HttpServletResponse;

import static com.emarte.regurgitator.extensions.web.HttpConstants.*;

public class HttpResponseUtil {
	private static final Log log = Log.getLog(HttpResponseUtil.class);

	public static void applyResponseData(Message message, HttpServletResponse httpServletResponse) {
		addResponseHeaders(message, httpServletResponse);
		addResponseMetadata(message, httpServletResponse);
	}

	private static void addResponseHeaders(Message message, HttpServletResponse response) {
		log.debug("Adding http response headers");
		Parameters context = message.getContext(RESPONSE_HEADERS_CONTEXT);

		for(Object id : context.ids()) {
			Object value = context.getValue(id);
			log.debug("Setting http response header '" + id + "' to value '" + value + "'");
			response.addHeader(String.valueOf(id), value.toString());
		}
	}

	private static void addResponseMetadata(Message message, HttpServletResponse httpServletResponse) {
		log.debug("Adding http response metadata");
		Parameters context = message.getContext(RESPONSE_METADATA_CONTEXT);

		if(context.contains(STATUS_CODE)) {
			Object value = context.getValue(STATUS_CODE);
			log.debug("Setting status code '" + value + "'");
			httpServletResponse.setStatus(objToInt(value));
		}

		if(context.contains(CONTENT_TYPE)) {
			Object value = context.getValue(CONTENT_TYPE);
			log.debug("Setting content type to '" + value + "'");
			httpServletResponse.setContentType(value.toString());
		}

		if(context.contains(CHARACTER_ENCODING)) {
			Object value = context.getValue(CHARACTER_ENCODING);
			log.debug("Setting character encoding to '" + value + "'");
			httpServletResponse.setCharacterEncoding(value.toString());
		}

		if(context.contains(CONTENT_LENGTH)) {
			Object value = context.getValue(CONTENT_LENGTH);
			log.debug("Setting content length to '" + value + "'");
			httpServletResponse.setContentLength(objToInt(value));
		}
	}

	private static int objToInt(Object value) {
		return Integer.parseInt(String.valueOf(value));
	}
}
