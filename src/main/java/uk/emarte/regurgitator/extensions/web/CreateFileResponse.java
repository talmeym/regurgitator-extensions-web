/*
 * Copyright (C) 2017 Miles Talmey.
 * Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
 */
package uk.emarte.regurgitator.extensions.web;

import uk.emarte.regurgitator.core.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import static uk.emarte.regurgitator.core.CoreTypes.NUMBER;
import static uk.emarte.regurgitator.core.FileUtil.*;
import static uk.emarte.regurgitator.core.StringType.stringify;
import static uk.emarte.regurgitator.extensions.web.ExtensionsWebConfigConstants.RESPONSE_METADATA_CONTEXT;
import static uk.emarte.regurgitator.extensions.web.ExtensionsWebConfigConstants.STATUS_CODE;

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
