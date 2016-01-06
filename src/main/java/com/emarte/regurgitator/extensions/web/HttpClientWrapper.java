package com.emarte.regurgitator.extensions.web;

import com.emarte.regurgitator.core.Log;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.*;

import java.io.IOException;

public class HttpClientWrapper {
	private static final Log log = Log.getLog(HttpClientWrapper.class);

	private HttpClient httpClient;

	public HttpClientWrapper(String host, int port, String username, String password) {
		httpClient = new HttpClient();
		httpClient.setHostConfiguration(getHostConfiguration(host, port));

		if(username != null && password != null) {
			httpClient.getParams().setAuthenticationPreemptive(true);
			httpClient.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
		}
	}

	public String getHost() {
		return httpClient.getHostConfiguration().getHost();
	}

	public int getPort() {
		return httpClient.getHostConfiguration().getPort();
	}

	public int executeMethod(HttpMethod method) throws IOException {
		String host = getHost();
		log.debug("overriding host header with value '" + host + "'");
		method.setRequestHeader("host", host);
		return httpClient.executeMethod(method);
	}

	public HttpMethod newGetMethod() {
		return new GetMethod();
	}

	public PostMethod newPostMethod() {
		return new PostMethod();
	}

	public HttpMethod newPutMethod() {
		return new PutMethod();
	}

	public HttpMethod newDeleteMethod() {
		return new DeleteMethod();
	}

	private static HostConfiguration getHostConfiguration(String host, int port) {
		HostConfiguration hostConfiguration = new HostConfiguration();
		hostConfiguration.setHost(host, port);
		return hostConfiguration;
	}
}
