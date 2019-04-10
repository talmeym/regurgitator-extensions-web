/*
 * Copyright (C) 2017 Miles Talmey.
 * Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
 */
package com.emarte.regurgitator.extensions.web;

import com.emarte.regurgitator.core.Log;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;

import java.io.IOException;

import static com.emarte.regurgitator.core.Log.getLog;
import static org.apache.commons.httpclient.auth.AuthScope.ANY;

public class HttpClientWrapper {
    private static final Log log = getLog(HttpClientWrapper.class);

    private final HttpClient httpClient;
    private String username;

    public HttpClientWrapper(String protocol, String host, int port, String username, String password) {
        httpClient = new HttpClient();
        httpClient.setHostConfiguration(getHostConfiguration(protocol, host, port));

        if(username != null && password != null) {
            httpClient.getParams().setAuthenticationPreemptive(true);
            httpClient.getState().setCredentials(ANY, new UsernamePasswordCredentials(username, password));
            this.username = username;
        }
    }

    public String getHost() {
        return httpClient.getHostConfiguration().getHost();
    }

    public int getPort() {
        return httpClient.getHostConfiguration().getPort();
    }

    public String getUsername() {
        return username;
    }

    public int executeMethod(HttpMethod method) throws IOException {
        String host = getHost();
        log.debug("overriding host header with value '{}'", host);
        method.setRequestHeader("host", host);
        return httpClient.executeMethod(method);
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

    private static HostConfiguration getHostConfiguration(String protocol, String host, int port) {
        HostConfiguration hostConfiguration = new HostConfiguration();
        hostConfiguration.setHost(host, port, protocol);
        return hostConfiguration;
    }
}
