/*
 * Copyright (C) 2017 Miles Talmey.
 * Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
 */
package uk.emarte.regurgitator.extensions.web;

import org.junit.Test;
import uk.emarte.regurgitator.core.*;

import static junit.framework.Assert.assertEquals;
import static uk.emarte.regurgitator.extensions.web.ExtensionsWebConfigConstants.RESPONSE_METADATA_CONTEXT;
import static uk.emarte.regurgitator.extensions.web.ExtensionsWebConfigConstants.STATUS_CODE;

public class CreateFileResponseTest {
    private final CreateFileResponse toTest = new CreateFileResponse("id", new ValueSource(new ContextLocation("file-path"), null), null);
    private final CreateFileResponse staticToTest = new CreateFileResponse("id", new ValueSource(null, "/test.file"), null);
    private final CreateFileResponse prefixToTest = new CreateFileResponse("id", new ValueSource(new ContextLocation("file-path"), null), "/assets/");

    @Test
    public void testThis() throws RegurgitatorException {
        CollectingResponseCallBack callback = new CollectingResponseCallBack();
        Message message = new Message(callback);

        message.getParameters().setValue("file-path", "test.file");
        toTest.execute(message);
        assertEquals("file value", callback.getValue());

        message.getParameters().setValue("file-path", "//test.file");
        toTest.execute(message);
        assertEquals("file value", callback.getValue());
    }

    @Test
    public void testFileNotFound() throws RegurgitatorException {
        CollectingResponseCallBack callback = new CollectingResponseCallBack();
        Message message = new Message(callback);

        message.getParameters().setValue("file-path", "does.not.exist.file");
        toTest.execute(message);

        assertEquals("Not Found", callback.getValue());
        Parameters context = message.getContext(RESPONSE_METADATA_CONTEXT);
        assertEquals(404L, context.getValue(STATUS_CODE));
    }

    @Test
    public void testStaticValue() throws RegurgitatorException {
        CollectingResponseCallBack callback = new CollectingResponseCallBack();
        Message message = new Message(callback);

        staticToTest.execute(message);
        assertEquals("file value", callback.getValue());
    }

    @Test
    public void testPrefix() throws RegurgitatorException {
        CollectingResponseCallBack callback = new CollectingResponseCallBack();
        Message message = new Message(callback);

        message.getParameters().setValue("file-path", "dir/test.file");
        prefixToTest.execute(message);
        assertEquals("assets file value", callback.getValue());

        message.getParameters().setValue("file-path", "//dir/test.file");
        prefixToTest.execute(message);
        assertEquals("assets file value", callback.getValue());
    }

    private class CollectingResponseCallBack implements ResponseCallBack {
        private Object value;

        @Override
        public void respond(Message message, Object value) {
            this.value = value;
        }

        public Object getValue() {
            return value;
        }
    }
}
