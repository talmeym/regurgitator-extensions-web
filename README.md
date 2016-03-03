# regurgitator-extensions-web

regurgitator is a modular, light-weight, extendable java-based processing framework designed to 'regurgitate' canned or clever responses to incoming requests; useful for mocking or prototyping services.

start your reading here: [regurgitator-all](http://github.com/talmeym/regurgitator-all#regurgitator)

## steps

regurgitator-extensions-web provides the following steps:
- ``http-call`` make an http call, using parameter values for host, port, request-uri etc.
- ``create-http-response`` create a response, pre-populating parameters for content-type and a status code

## constructs

regurgitator-extensions-web provides the following constructs:
#### value processors
- ``query-param-processor`` process a parameter value, extracting values from it under a query param format

## servlets

regurgitator-extensions-web provides the following deployable servlets:
- ``com.emarte.regurgitator.RegurgitatorServlet`` accepts http requests, passing them on to regurgitator as a message
- ``com.emarte.regurgitator.GlobalDataServlet`` allows the setting of global parameters, applied to incoming http request messages
