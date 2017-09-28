package com.emarte.regurgitator.extensions.web;

import com.emarte.regurgitator.core.*;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static com.emarte.regurgitator.core.CoreTypes.STRING;
import static com.emarte.regurgitator.extensions.web.ExtensionsWebConfigConstants.*;
import static junit.framework.Assert.assertEquals;

public class HttpMessageProxyTest {
	private CollectingHttpClientWrapper wrapper = getWrapper();
	private HttpMessageProxy toTest = new HttpMessageProxy(wrapper);

	@Test
	public void testDefaults() throws RegurgitatorException {
		Message response = toTest.proxyMessage(new Message(null));
		assertEquals("GET[null,request-headers={},response-headers={rsh1=rsv1, rsh2=rsv2},connection-released=true]", wrapper.toString());
		assertEquals("message[response-metadata[status-code=200,],response-headers[rsh1=rsv1,rsh2=rsv2,],response-payload[text=response body,],]", response.toString());
	}

	@Test
	public void testDefaultsPost() throws RegurgitatorException {
		Message response = toTest.proxyMessage(buildDefaultPostMessage());
		assertEquals("POST[null,request-headers={},response-headers={rsh1=rsv1, rsh2=rsv2},connection-released=true]", wrapper.toString());
		assertEquals("message[response-metadata[status-code=200,],response-headers[rsh1=rsv1,rsh2=rsv2,],response-payload[text=response body,],]", response.toString());
	}

	@Test
	public void testGet() throws RegurgitatorException {
		Message response = toTest.proxyMessage(buildMessage(GET));
		assertEquals("GET[/path/path,request-headers={rqh1=rqv1, rqh2=rqv2},response-headers={rsh1=rsv1, rsh2=rsv2},connection-released=true]", wrapper.toString());
		assertEquals("message[response-metadata[status-code=200,],response-headers[rsh1=rsv1,rsh2=rsv2,],response-payload[text=response body,],]", response.toString());
	}

	@Test
	public void testPut() throws RegurgitatorException {
        Message message = buildMessage(PUT);
        message.getContext(REQUEST_PAYLOAD_CONTEXT).setValue(TEXT, STRING, "request body");
        Message response = toTest.proxyMessage(message);
		assertEquals("PUT[/path/path,request-body=text/plain; charset=UTF-8:request body,request-headers={rqh2=rqv2, rqh1=rqv1},response-headers={rsh1=rsv1, rsh2=rsv2},connection-released=true]", wrapper.toString());
		assertEquals("message[response-metadata[status-code=200,],response-headers[rsh1=rsv1,rsh2=rsv2,],response-payload[text=response body,],]", response.toString());
	}

	@Test
	public void testPost() throws RegurgitatorException {
		Message message = buildMessage(POST);
		message.getContext(REQUEST_PAYLOAD_CONTEXT).setValue(TEXT, STRING, "request body");
		Message response = toTest.proxyMessage(message);
		assertEquals("POST[/path/path,request-body=text/plain; charset=UTF-8:request body,request-headers={rqh2=rqv2, rqh1=rqv1},response-headers={rsh1=rsv1, rsh2=rsv2},connection-released=true]", wrapper.toString());
		assertEquals("message[response-metadata[status-code=200,],response-headers[rsh1=rsv1,rsh2=rsv2,],response-payload[text=response body,],]", response.toString());
	}

	@Test
	public void testDelete() throws RegurgitatorException {
		Message response = toTest.proxyMessage(buildMessage(DELETE));
		assertEquals("DELETE[/path/path,request-headers={rqh1=rqv1, rqh2=rqv2},response-headers={rsh1=rsv1, rsh2=rsv2},connection-released=true]", wrapper.toString());
		assertEquals("message[response-metadata[status-code=200,],response-headers[rsh1=rsv1,rsh2=rsv2,],response-payload[text=response body,],]", response.toString());
	}

	private CollectingHttpClientWrapper getWrapper() {
		TreeMap<String, String> responseHeaders = new TreeMap<String, String>();
		responseHeaders.put("rsh1", "rsv1");
		responseHeaders.put("rsh2", "rsv2");
		return new CollectingHttpClientWrapper("response body", responseHeaders, 200);
	}

	private Message buildMessage(String method) throws RegurgitatorException {
		Message message = new Message(null);
		message.getContext(REQUEST_METADATA_CONTEXT).setValue(METHOD, STRING, method);
		message.getContext(REQUEST_METADATA_CONTEXT).setValue(PATH_INFO, STRING, "/path/path");
		message.getContext(REQUEST_METADATA_CONTEXT).setValue(CONTENT_TYPE, STRING, "text/plain");
		message.getContext(REQUEST_METADATA_CONTEXT).setValue(CHARACTER_ENCODING, STRING, "UTF-8");
		message.getContext(REQUEST_HEADERS_CONTEXT).setValue("rqh1", STRING, "rqv1");
		message.getContext(REQUEST_HEADERS_CONTEXT).setValue("rqh2", STRING, "rqv2");
		return message;
	}

	private Message buildDefaultPostMessage() {
		Message message = new Message(null);
		message.getContext(REQUEST_METADATA_CONTEXT).setValue(METHOD, POST);
		return message;
	}

	private class CollectingHttpClientWrapper extends HttpClientWrapper {
		private String responseBody;
		private Map<String, String> responseHeaders;
		private int statusCode;

		private HttpMethod methodRequested;

		public CollectingHttpClientWrapper(String responseBody, Map<String, String> responseHeaders, int statusCode) {
			super("", -1, null, null);
			this.responseBody = responseBody;
			this.responseHeaders = responseHeaders;
			this.statusCode = statusCode;
		}

		@Override
		public HttpMethod newGetMethod() {
			methodRequested = new MyHttpMethod("GET", responseBody, responseHeaders, statusCode);
			return methodRequested;
		}

		@Override
		public PostMethod newPostMethod() {
			MyPostMethod postMethod = new MyPostMethod("POST", responseBody, responseHeaders, statusCode);
			methodRequested = postMethod;
			return postMethod;
		}

		@Override
		public PutMethod newPutMethod() {
			MyPutMethod putMethod = new MyPutMethod("PUT", responseBody, responseHeaders, statusCode);
			methodRequested = putMethod;
			return putMethod;
		}

		@Override
		public HttpMethod newDeleteMethod() {
			methodRequested = new MyHttpMethod("DELETE", responseBody, responseHeaders, statusCode);
			return methodRequested;
		}

		@Override
		public int executeMethod(HttpMethod method) throws IOException {
			if(method != methodRequested) {
				throw new IllegalArgumentException("wrong method");
			}

			return method.execute(null, null);
		}

		@Override
		public String toString() {
			return methodRequested.toString();
		}
	}
}
