package com.emarte.regurgitator.extensions.web;

import com.emarte.regurgitator.core.*;

import java.util.List;

import static com.emarte.regurgitator.core.Log.getLog;

public class HttpCall extends Container<Step> implements Step {
    private final Log log = getLog(this);
    private final HttpMessageProxy messageProxy;

    public HttpCall(Object id, HttpMessageProxy httpMessageProxy, List<Step> steps) {
        super(id, steps);
        messageProxy = httpMessageProxy;
    }

    @Override
    public void execute(Message message) throws RegurgitatorException {
        log.debug("Sending message to message proxy");
        Message responseMessage = messageProxy.proxyMessage(message);

        if(size() > 0) {
            log.debug("Processing response");

            for(Step step : getAll()) {
                log.debug("Executing step '{}'", step.getId());
                step.execute(responseMessage);
            }
        }
    }
}
