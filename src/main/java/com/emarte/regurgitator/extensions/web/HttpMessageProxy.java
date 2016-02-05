package com.emarte.regurgitator.extensions.web;

import com.emarte.regurgitator.core.*;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;

import java.io.*;

import static com.emarte.regurgitator.core.CoreTypes.STRING;
import static com.emarte.regurgitator.core.StringType.stringify;
import static com.emarte.regurgitator.extensions.web.ExtensionsWebConfigConstants.*;
import static java.lang.String.valueOf;

public class HttpMessageProxy {
    private static final Log log = Log.getLog(HttpMessageProxy.class);

	private HttpClientWrapper clientWrapper;

    public HttpMessageProxy(String host, int port, String username, String password) {
		this(new HttpClientWrapper(host, port, username, password));
	}

	protected HttpMessageProxy(HttpClientWrapper clientWrapper) {
		this.clientWrapper = clientWrapper;
	}

    public Message proxyMessage(Message message) throws RegurgitatorException {
		long start = System.currentTimeMillis();
		String username = clientWrapper.getUsername();
		log.debug("Proxying message to '" + clientWrapper.getHost() + ":" + clientWrapper.getPort() + "'" + (username != null ? " with credentials for '" + username + "'": ""));
        HttpMethod method = getMethod(message);
        setPath(method, message);
        addHeaders(method, message.getContext(REQUEST_HEADERS_CONTEXT));

        try {
			log.debug("Executing method");
            int status = clientWrapper.executeMethod(method);
			log.debug("Creating new message");
			Message newMessage = new Message(message, true, true);
            setStatusCode(status, newMessage);
            setPayload(method, newMessage);
            addHeaders(method, newMessage);
            method.releaseConnection();
            return newMessage;
        } catch (IOException e) {
            throw new RegurgitatorException("Exception making http call", e);
        } finally {
			long end = System.currentTimeMillis();
			log.debug("All done: " + (end - start) + " milliseconds");
		}
    }

	private HttpMethod getMethod(Message message) throws RegurgitatorException {
		Parameters context = message.getContext(REQUEST_METADATA_CONTEXT);
		String method = stringify(context.getValue(METHOD));

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
				log.debug("Adding payload to post method: '" + requestBody + "'");
				try {
				postMethod.setRequestEntity(new StringRequestEntity(requestBody, stringify(context.getValue(CONTENT_TYPE)), stringify(context.getValue(CHARACTER_ENCODING))));
				} catch(UnsupportedEncodingException uee) {
					throw new RegurgitatorException("Error encoding post body", uee);
				}
			}

			return postMethod;
		}

		if(PUT.equals(method)) {
			log.debug("Using put method");
			return clientWrapper.newPutMethod();
		}

		if(DELETE.equals(method)) {
			log.debug("Using delete method");
			return clientWrapper.newDeleteMethod();
		}

		throw new RegurgitatorException("Invalid method in request metadata: " + method);
	}

	private static void setPath(HttpMethod method, Message message) throws RegurgitatorException {
        String path = stringify(message.getContextValue(new ContextLocation(REQUEST_METADATA_CONTEXT, PATH_INFO)));
		log.debug("Setting method path to '" + path + "'");
        method.setPath(path);
    }

    private static void addHeaders(HttpMethod method, Parameters context) {
        for(Object id: context.ids()) {
            String value = stringify(context.getValue(id));
            log.debug("Adding request header '" + id + " with value '" + value + "' to method");
            method.addRequestHeader(stringify(id), value);
        }
    }

	private static void setStatusCode(int status, Message message) throws RegurgitatorException {
		log.debug("Setting response status code to '" + status + "'");
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

    private static void addHeaders(HttpMethod method, Message message) throws RegurgitatorException {
        Header[] responseHeaders = method.getResponseHeaders();

        if(responseHeaders != null && responseHeaders.length > 0) {
            log.debug("Adding response headers");
			Parameters context = message.getContext(RESPONSE_HEADERS_CONTEXT);

            for(Header header : responseHeaders) {
				context.setValue(header.getName(), STRING, header.getValue());
			}
        }
    }
}
