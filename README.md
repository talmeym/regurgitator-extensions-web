# regurgitator-extensions-web

regurgitator is a modular, light-weight, extendable java-based processing framework designed to 'regurgitate' canned or clever responses to incoming requests; useful for mocking or prototyping services.

start your reading here: [regurgitator-all](http://github.com/talmeym/regurgitator-all#regurgitator)

## regurgitator over http

regurgitator allows the mocking of http services, using the following deployable servlets:
- ``com.emarte.regurgitator.extensions.web.RegurgitatorServlet`` accepts http requests, passes them to regurgitator as messages, returns configured http responses
- ``com.emarte.regurgitator.extensions.web.GlobalMetadataServlet`` allows the setting of global parameters, applied to incoming messages before processing

``RegurgitatorServlet`` maps the following java http request attributes to message parameters:

|attribute|context|parameter|
|---|---|---|
|HttpRequest.serverName|request-metadata|server-name|
|HttpRequest.method|request-metadata|method|
|HttpRequest.requestURI|request-metadata|request-uri|
|HttpRequest.queryString|request-metadata|query-string|
|HttpRequest.authType|request-metadata|auth-type|
|HttpRequest.contextPath|request-metadata|context-path|
|HttpRequest.pathInfo|request-metadata|path-info|
|HttpRequest.pathTranslated|request-metadata|path-translated|
|HttpRequest.remoteUser|request-metadata|remote-user|
|HttpRequest.requestedSessionId|request-metadata|requested-session-id|
|HttpRequest.servletPath|request-metadata|servlet-path|
|HttpRequest.characterEncoding|request-metadata|character-encoding|
|HttpRequest.contentType|request-metadata|content-type|
|HttpRequest.localAddr|request-metadata|local-address|
|HttpRequest.localName|request-metadata|local-name|
|HttpRequest.protocol|request-metadata|protocol|
|HttpRequest.remoteAddr|request-metadata|remote-address|
|HttpRequest.remoteHost|request-metadata|remote-host|
|HttpRequest.scheme|request-metadata|scheme|
|HttpRequest.contentLength|request-metadata|content-length|
|HttpRequest.localPort|request-metadata|local-port|
|HttpRequest.serverPort|request-metadata|server-port|
|HttpRequest.session.id|request-metadata|http-session-id|

``RegurgtiatorServlet`` maps the following message parameters to HttpResponse attributes: 

|context|parameter|attribute|
|---|---|---|
|response-metadata|text|HttpResponse payload|
|response-metadata|status-code|HttpRespone.statusCode|


        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        

### example web.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app id="WebApp_ID" version="2.4"
		 xmlns="http://java.sun.com/xml/ns/j2ee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		 xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd">
	<display-name>My Mocked Server</display-name>

	<servlet>
		<servlet-name>RegurgitatorServlet</servlet-name>
		<servlet-class>com.emarte.regurgitator.extensions.web.RegurgitatorServlet</servlet-class>
		<init-param>
			<param-name>config-location</param-name>
			<param-value>classpath:/config.xml</param-value>
		</init-param>
		<load-on-startup>1</load-on-startup>
	</servlet>

	<servlet-mapping>
		<servlet-name>RegurgitatorServlet</servlet-name>
		<url-pattern>/*</url-pattern>
	</servlet-mapping>
</web-app>
```

## steps

regurgitator-extensions-web provides the following steps:
- ``http-call`` make an outward http call, using parameter values for payload, request metadata, headers etc.
- ``create-http-response`` create a response, pre-populating parameters for content-type and status code

## constructs

regurgitator-extensions-web provides the following constructs:
#### value processors
- ``query-param-processor`` process a parameter value, extracting values from it using a query param format

