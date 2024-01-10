/*
 * Copyright (C) 2017 Miles Talmey.
 * Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
 */
package uk.emarte.regurgitator.extensions.web;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.AuthState;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

@SuppressWarnings({"deprecation"})
class MyHttpMethod implements HttpMethod {
    private final String name;
    private String path;
    private final Map<String, String> requestHeaders = new TreeMap<String, String>();
    private final String responseBody;
    private final Map<String, String> responseHeaders;
    private final int statusCode;
    private boolean connectionReleased;

    private final UnsupportedOperationException exception = new UnsupportedOperationException("not implemented");

    public MyHttpMethod(String name, String responseBody, Map<String, String> responseHeaders, int statusCode) {
        this.name = name;
        this.responseBody = responseBody;
        this.responseHeaders = responseHeaders;
        this.statusCode = statusCode;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public HostConfiguration getHostConfiguration() {
        throw exception;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public URI getURI() {
        throw exception;
    }

    @Override
    public void setURI(URI uri) {
        throw exception;
    }

    @Override
    public void setStrictMode(boolean strictMode) {
        throw exception;
    }

    @Override
    public boolean isStrictMode() {
        throw exception;
    }

    @Override
    public void setRequestHeader(String headerName, String headerValue) {
        requestHeaders.put(headerName, headerValue);
    }

    @Override
    public void setRequestHeader(Header header) {
        requestHeaders.put(header.getName(), header.getValue());
    }

    @Override
    public void addRequestHeader(String headerName, String headerValue) {
        requestHeaders.put(headerName, headerValue);
    }

    @Override
    public void addRequestHeader(Header header) {
        requestHeaders.put(header.getName(), header.getValue());
    }

    @Override
    public Header getRequestHeader(String headerName) {
        return requestHeaders.containsKey(headerName) ? new Header(headerName, requestHeaders.get(headerName)) : null;
    }

    @Override
    public void removeRequestHeader(String headerName) {
        requestHeaders.remove(headerName);
    }

    @Override
    public void removeRequestHeader(Header header) {
        requestHeaders.remove(header.getName());
    }

    @Override
    public boolean getFollowRedirects() {
        throw exception;
    }

    @Override
    public void setFollowRedirects(boolean followRedirects) {
        throw exception;
    }

    @Override
    public void setQueryString(String queryString) {
        throw exception;
    }

    @Override
    public void setQueryString(NameValuePair[] params) {
        throw exception;
    }

    @Override
    public String getQueryString() {
        throw exception;
    }

    @Override
    public Header[] getRequestHeaders() {
        return buildHeaders(requestHeaders);
    }

    @Override
    public Header[] getRequestHeaders(String headerName) {
        return new Header[]{ new Header(headerName, requestHeaders.get(headerName)) };
    }

    private Header[] buildHeaders(Map<String, String> headers) {
        Header[] h = new Header[headers.size()];
        int index = 0;

        for(Map.Entry<String, String> entry : headers.entrySet()) {
            h[index++] = new Header(entry.getKey(), entry.getValue());
        }

        return h;
    }

    @Override
    public boolean validate() {
        throw exception;
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String getStatusText() {
        throw exception;
    }

    @Override
    public Header[] getResponseHeaders() {
        return buildHeaders(responseHeaders);
    }

    @Override
    public Header getResponseHeader(String headerName) {
        return new Header(headerName, responseHeaders.get(headerName));
    }

    @Override
    public Header[] getResponseHeaders(String headerName) {
        return new Header[]{ new Header(headerName, responseHeaders.get(headerName)) };
    }

    @Override
    public Header[] getResponseFooters() {
        throw exception;
    }

    @Override
    public Header getResponseFooter(String footerName) {
        throw exception;
    }

    @Override
    public byte[] getResponseBody() {
        return responseBody.getBytes();
    }

    @Override
    public String getResponseBodyAsString() {
        return responseBody;
    }

    @Override
    public InputStream getResponseBodyAsStream() {
        return new ByteArrayInputStream(responseBody.getBytes());
    }

    @Override
    public boolean hasBeenUsed() {
        throw exception;
    }

    @Override
    public int execute(HttpState state, HttpConnection connection) {
        return statusCode;
    }

    @Override
    public void abort() {
        throw exception;
    }

    @Override
    public void recycle() {
        throw exception;
    }

    @Override
    public void releaseConnection() {
        connectionReleased = true;
    }

    @Override
    public void addResponseFooter(Header footer) {
        throw exception;
    }

    @Override
    public StatusLine getStatusLine() {
        throw exception;
    }

    @Override
    public boolean getDoAuthentication() {
        throw exception;
    }

    @Override
    public void setDoAuthentication(boolean doAuthentication) {
        throw exception;
    }

    @Override
    public HttpMethodParams getParams() {
        throw exception;
    }

    @Override
    public void setParams(HttpMethodParams params) {
        throw exception;
    }

    @Override
    public AuthState getHostAuthState() {
        throw exception;
    }

    @Override
    public AuthState getProxyAuthState() {
        throw exception;
    }

    @Override
    public boolean isRequestSent() {
        throw exception;
    }

    @Override
    public String toString() {

        return name + "[" + path + ",request-headers=" + requestHeaders + ",response-headers=" + responseHeaders + ",connection-released=" + connectionReleased + "]";
    }
}
