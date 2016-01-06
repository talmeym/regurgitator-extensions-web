package com.emarte.regurgitator.extensions.web;

import com.emarte.regurgitator.core.*;

public class HttpCall extends Identifiable implements Step {
    private final Log log = Log.getLog(this);
    private final HttpMessageProxy messageProxy;
	private final Step responseProcessing;

    public HttpCall(Object id, HttpMessageProxy httpMessageProxy, Step responseProcessing) {
        super(id);
        messageProxy = httpMessageProxy;
		this.responseProcessing = responseProcessing;
    }

    @Override
    public void execute(Message message) throws RegurgitatorException {
        log.debug("Sending message to message proxy");
        Message response = messageProxy.proxyMessage(message);
		log.debug("Processing response");
        responseProcessing.execute(response);
    }
}
