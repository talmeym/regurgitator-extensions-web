# regurgitator-extensions-web

regurgitator is a modular, light-weight, extendable java-based processing framework designed to 'regurgitate' canned or clever responses to incoming requests; useful for mocking or prototyping services.

start your reading here: [regurgitator-all](http://github.com/talmeym/regurgitator-all#regurgitator)

## regurgitator over http

regurgitator allows the mocking of http services, using the following deployable servlets:
- ``com.emarte.regurgitator.extensions.web.RegurgitatorServlet`` accepts http requests, passes them to regurgitator as messages, returns configured http responses
- ``com.emarte.regurgitator.extensions.web.GlobalMetadataServlet`` allows the setting of global parameters, applied to incoming messages before processing

### regurgitator servlet

***http call*** => ***RegurgitatorServlet*** => ***message*** => [regurgitator] => ***message*** => ***RegurgitatorServlet*** => ***http response***

``RegurgitatorServlet`` accepts http calls and maps request attributes to message parameters, then passes the message to regurgitator for processing. When regurgitator returns its first (and only) response, ``RegurgitatorServlet`` maps any populated response parameters to http response attributes, and returns the response to the caller.

### request mappings

``RegurgitatorServlet`` maps the following java http request attributes to message parameters:

|attribute|context|parameter|type|
|---|---|---|---|
|``HttpServletRequest.serverName``|``request-metadata``|``server-name``|``STRING``|
|``HttpServletRequest.method``|``request-metadata``|``method``|``STRING``|
|``HttpServletRequest.requestURI``|``request-metadata``|``request-uri``|``STRING``|
|``HttpServletRequest.queryString``|``request-metadata``|``query-string``|``STRING``|
|``HttpServletRequest.authType``|``request-metadata``|``auth-type``|``STRING``|
|``HttpServletRequest.contextPath``|``request-metadata``|``context-path``|``STRING``|
|``HttpServletRequest.pathInfo``|``request-metadata``|``path-info``|``STRING``|
|``HttpServletRequest.pathTranslated``|``request-metadata``|``path-translated``|``STRING``|
|``HttpServletRequest.remoteUser``|``request-metadata``|``remote-user``|``STRING``|
|``HttpServletRequest.requestedSessionId``|``request-metadata``|``requested-session-id``|``STRING``|
|``HttpServletRequest.servletPath``|``request-metadata``|``servlet-path``|``STRING``|
|``HttpServletRequest.characterEncoding``|``request-metadata``|``character-encoding``|``STRING``|
|``HttpServletRequest.contentType``|``request-metadata``|``content-type``|``STRING``|
|``HttpServletRequest.localAddr``|``request-metadata``|``local-address``|``STRING``|
|``HttpServletRequest.localName``|``request-metadata``|``local-name``|``STRING``|
|``HttpServletRequest.protocol``|``request-metadata``|``protocol``|``STRING``|
|``HttpServletRequest.remoteAddr``|``request-metadata``|``remote-address``|``STRING``|
|``HttpServletRequest.remoteHost``|``request-metadata``|``remote-host``|``STRING``|
|``HttpServletRequest.scheme``|``request-metadata``|``scheme``|``STRING``|
|``HttpServletRequest.contentLength``|``request-metadata``|``content-length``|``NUMBER``|
|``HttpServletRequest.localPort``|``request-metadata``|``local-port``|``NUMBER``|
|``HttpServletRequest.serverPort``|``request-metadata``|``server-port``|``NUMBER``|
|``HttpServletRequest.session.id``|``request-metadata``|``http-session-id``|``STRING``|
|``HttpServletRequest payload``|``request-payload``|``text``|``STRING``|
|``HttpServletRequest headers``|``request-headers``|``[header-name]``|``STRING``|

### response mappings

``RegurgtiatorServlet`` maps the following message parameters to HttpResponse attributes: 

|context|parameter|type|attribute|
|---|---|---|---|
|``response-payload``|``text``|``STRING``|``HttpServletResponse payload``|
|``response-headers``|``[header-name]``|``STRING``|``HttpServletResponse headers``|
|``response-metadata``|``status-code``|``NUMBER``|``HttpServletResponse.status``|
|``response-metadata``|``content-type``|``STRING``|``HttpServletResponse.contentLength``|
|``response-metadata``|``character-encoding``|``STRING``|``HttpServletResponse.characterEncoding``|
|``response-metadata``|``content-length``|``NUMBER``|``HttpServletResponse.contentLength``|

### global metadata servlet

the ``GlobalMetadataServlet`` allows for the maintenance of a set of global parameters, that are applied by regurgitator upon an incoming request to the resulting message object before it's passed to the configured steps for processing, and can then be referenced by step configurations.

it can load a set of parameters from a properties file upon initialisation and can have parameters added, modified and deleted via http calls to the servlet.

the format for the properties file is as such:

```
parameter-name=parameter-value
parameter-name.type=STRING
```

### example web.xml

below is an example web.xml for mapping all incoming http requests to ``RegurgitatorServlet``

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app id="WebApp_ID" version="2.4"
		 xmlns="http://java.sun.com/xml/ns/j2ee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		 xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd">
	<display-name>My Mocked Service</display-name>

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
- ``http-call`` ([xml](https://github.com/talmeym/regurgitator-extensions-web-xml#http-call), [json](https://github.com/talmeym/regurgitator-extensions-web-json#http-call)) make an outward http call, using parameter values for payload, request metadata, headers etc.
- ``create-http-response`` ([xml](https://github.com/talmeym/regurgitator-extensions-web-xml#create-http-response), [json](https://github.com/talmeym/regurgitator-extensions-web-json#create-http-response)) create a response, pre-populating parameters for content-type and status code

## constructs

regurgitator-extensions-web provides the following constructs:
#### value processors
- ``query-param-processor`` ([xml](https://github.com/talmeym/regurgitator-extensions-web-xml#query-param-processor), [json](https://github.com/talmeym/regurgitator-extensions-web-json#query-param-processor)) process a parameter value, extracting values from it using a query param format

