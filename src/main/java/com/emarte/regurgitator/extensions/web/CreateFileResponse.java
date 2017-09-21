package com.emarte.regurgitator.extensions.web;

import com.emarte.regurgitator.core.*;

import java.io.*;

import static com.emarte.regurgitator.core.CoreTypes.NUMBER;
import static com.emarte.regurgitator.core.FileUtil.*;
import static com.emarte.regurgitator.core.StringType.stringify;
import static com.emarte.regurgitator.extensions.web.ExtensionsWebConfigConstants.*;

public class CreateFileResponse extends Identifiable implements Step {
	private static final Log log = Log.getLog(CreateFileResponse.class);
	private static final String NOT_FOUND = "Not Found";

	private final ValueSource valueSource;
	private final String pathPrefix;

	public CreateFileResponse(Object id, ValueSource valueSource, String pathPrefix) {
		super(id);
		this.valueSource = valueSource;
		this.pathPrefix = pathPrefix;
	}

	@Override
	public void execute(Message message) throws RegurgitatorException {
		String path = stringify(valueSource.getValue(message, log));
		String filePath = CLASSPATH_PREFIX + (pathPrefix != null ? checkSlashes(pathPrefix) : "") + checkSlashes(path);

		try {
			String fileContents = streamToString(getInputStreamForFile(filePath));
			message.getResponseCallback().respond(message, fileContents);
		} catch (FileNotFoundException e) {
			Parameters responseMetadata = message.getContext(RESPONSE_METADATA_CONTEXT);
			responseMetadata.setValue(STATUS_CODE, NUMBER, 404L);
			message.getResponseCallback().respond(message, NOT_FOUND);
		} catch (IOException e) {
			throw new RegurgitatorException("Error loading file: " + filePath, e);
		}
	}

	private String checkSlashes(String string) {
		while (string.endsWith("/")) {
			string = string.substring(0, string.length() - 1);
		}

		while (string.startsWith("/")) {
			string = string.substring(1);
		}

		return "/" + string;
	}
}
