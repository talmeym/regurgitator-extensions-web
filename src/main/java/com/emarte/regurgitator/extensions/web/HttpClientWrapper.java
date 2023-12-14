/*
 * Copyright (C) 2017 Miles Talmey.
 * Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
 */
package com.emarte.regurgitator.extensions.web;

import com.emarte.regurgitator.core.Log;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.emarte.regurgitator.core.Log.getLog;
import static java.util.Arrays.asList;
import static org.apache.commons.httpclient.auth.AuthScope.ANY;

class HttpClientWrapper {
    private static final Log log = getLog(HttpClientWrapper.class);

    private final HostConfiguration hostConfiguration;
    private final UsernamePasswordCredentials credentials;
    private final ThreadLocal<List<Cookie>> threadLocalCookies = new ThreadLocal<>();

    HttpClientWrapper(String protocol, String host, int port, String username, String password) {
        hostConfiguration = getHostConfiguration(protocol, host, port);
        credentials = username != null && password != null ? new UsernamePasswordCredentials(username, password) : null;
    }

    public String getHost() {
        return hostConfiguration.getHost();
    }

    public int getPort() {
        return hostConfiguration.getPort();
    }

    public String getUsername() {
        return credentials != null ? credentials.getUserName() : null;
    }

    int executeMethod(HttpMethod method, Cookie[] cookies) throws IOException {
        log.debug("Creating new http client");
        HttpClient httpClient = new HttpClient();
        log.debug("Setting host credential");
        httpClient.setHostConfiguration(hostConfiguration);

        if(credentials != null) {
            log.debug("Setting user credentials");
            httpClient.getState().setCredentials(ANY, credentials);
        }

        log.debug("Overriding host header with value '{}'", hostConfiguration.getHost());
        method.setRequestHeader("host", hostConfiguration.getHost());
        log.debug("Adding {} cookies", cookies.length);
        httpClient.getState().addCookies(cookies);
        log.debug("Making http call");
        long start = System.currentTimeMillis();
        int statusCode = httpClient.executeMethod(method);
        long end = System.currentTimeMillis();
        log.debug("Status code {} received in {} milliseconds", statusCode, end - start);

        List<Cookie> afterCookies = asList(httpClient.getState().getCookies());
        List<Cookie> lastCookies = threadLocalCookies.get();

        if(lastCookies == null) {
            lastCookies = new ArrayList<>();
            threadLocalCookies.set(lastCookies);
        }

        lastCookies.clear();
        lastCookies.addAll(afterCookies);
        lastCookies.removeAll(asList(cookies));
        log.debug("{} new / modified cookies received", lastCookies.size());
        return statusCode;
    }

    public HttpMethod newGetMethod() {
        return new GetMethod();
    }

    public PostMethod newPostMethod() {
        return new PostMethod();
    }

    public PutMethod newPutMethod() {
        return new PutMethod();
    }

    public HttpMethod newDeleteMethod() {
        return new DeleteMethod();
    }

    public List<Cookie> getLastCookies() {
        return threadLocalCookies.get();
    }

    private static HostConfiguration getHostConfiguration(String protocol, String host, int port) {
        HostConfiguration hostConfiguration = new HostConfiguration();
        hostConfiguration.setHost(host, port, protocol);
        return hostConfiguration;
    }
}
