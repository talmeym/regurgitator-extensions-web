package com.emarte.regurgitator.extensions.web;

public interface ExtensionsWebConfigConstants {
	// contexts
    public static final String REQUEST_PAYLOAD_CONTEXT = "request-payload";
    public static final String REQUEST_HEADERS_CONTEXT = "request-headers";
    public static final String REQUEST_METADATA_CONTEXT = "request-metadata";
    public static final String RESPONSE_PAYLOAD_CONTEXT = "response-payload";
    public static final String RESPONSE_HEADERS_CONTEXT = "response-headers";
    public static final String RESPONSE_METADATA_CONTEXT = "response-metadata";
    public static final String REQUEST_COOKIES_CONTEXT = "request-cookies";
	public static final String GLOBAL_METADATA_CONTEXT = "global-metadata";

	// metadata
    public static final String SERVER_NAME = "server-name";
    public static final String METHOD = "method";
    public static final String REQUEST_URI = "request-uri";
    public static final String QUERY_STRING = "query-string";
    public static final String AUTH_TYPE = "auth-type";
    public static final String CONTEXT_PATH = "context-path";
    public static final String PATH_INFO = "path-info";
    public static final String PATH_TRANSLATED = "path-translated";
    public static final String REMOTE_USER = "remote-user";
    public static final String REQUESTED_SESSION_ID = "requested-session-id";
    public static final String SERVLET_PATH = "servlet-path";
    public static final String CHARACTER_ENCODING = "character-encoding";
    public static final String CONTENT_TYPE = "content-type";
    public static final String LOCAL_ADDRESS = "local-address";
    public static final String LOCAL_NAME = "local-name";
    public static final String PROTOCOL = "protocol";
    public static final String REMOTE_ADDRESS = "remote-address";
    public static final String REMOTE_HOST = "remote-host";
    public static final String SCHEME = "scheme";
    public static final String CONTENT_LENGTH = "content-length";
    public static final String LOCAL_PORT = "local-port";
    public static final String SERVER_PORT = "server-port";
    public static final String HTTP_SESSION_ID = "http-session-id";
    public static final String STATUS_CODE = "status-code";
    public static final String TEXT = "text";

	// methods
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";

	// query-string
	public static final String KEY = "key";
}
