/*
 * Copyright (C) 2017 Miles Talmey.
 * Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
 */
package uk.emarte.regurgitator.extensions.web;

import uk.emarte.regurgitator.core.*;

import static uk.emarte.regurgitator.core.CoreTypes.NUMBER;
import static uk.emarte.regurgitator.core.Log.getLog;
import static uk.emarte.regurgitator.extensions.web.ExtensionsWebConfigConstants.*;

public class CreateHttpResponse extends Identifiable implements Step {
    private final Log log = getLog(this);
    private final CreateResponse response;
    private final long statusCode;
    private final String contentType;

    public CreateHttpResponse(CreateResponse response, long statusCode, String contentType) {
        super(response.getId());
        this.response = response;
        this.statusCode = statusCode;
        this.contentType = contentType;
    }

    @Override
    public void execute(Message message) throws RegurgitatorException {
        Parameters responseMetadata = message.getContext(RESPONSE_METADATA_CONTEXT);

        if(statusCode != -1) {
            log.debug("Setting status code");
            responseMetadata.setValue(STATUS_CODE, NUMBER, statusCode);
        }

        if(contentType != null) {
            log.debug("Setting content type");
            responseMetadata.setValue(CONTENT_TYPE, contentType);
        }

        response.execute(message, log);
    }
}
