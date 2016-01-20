package com.emarte.regurgitator.extensions.web;

import com.emarte.regurgitator.core.*;

import static com.emarte.regurgitator.core.CoreTypes.NUMBER;
import static com.emarte.regurgitator.core.CoreTypes.STRING;
import static com.emarte.regurgitator.extensions.web.ExtensionsWebConfigConstants.*;

public class CreateHttpResponse extends Identifiable implements Step {
	private final Log log = Log.getLog(this);
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
			responseMetadata.setValue(STATUS_CODE, NUMBER, statusCode);
		}

		if(contentType != null) {
			responseMetadata.setValue(CONTENT_TYPE, STRING, contentType);
		}

		response.execute(message, log);
	}
}
