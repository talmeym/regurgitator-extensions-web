/*
 * Copyright (C) 2017 Miles Talmey.
 * Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
 */
package uk.emarte.regurgitator.extensions.web;

import org.junit.Test;
import uk.emarte.regurgitator.core.CoreTypes;
import uk.emarte.regurgitator.core.Parameter;
import uk.emarte.regurgitator.core.ParameterType;

import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static uk.emarte.regurgitator.core.FileUtil.getInputStreamForFile;
import static uk.emarte.regurgitator.extensions.web.HttpGlobalUtil.addGlobalParametersFromProperties;

public class HttpGlobalUtilTest {
    @Test
    public void testUtil() throws Exception {
        String propertiesLocation = "classpath:/global.properties";
        Properties properties = new Properties();
        properties.load(getInputStreamForFile(propertiesLocation));
        addGlobalParametersFromProperties(propertiesLocation, properties);
        Collection<Parameter> parameters = HttpGlobalUtil.getAllGlobalParameters();
        assertEquals(4, parameters.size());
        assertParameter("string", "this is a string", CoreTypes.STRING, parameters);
        assertParameter("number", 4L, CoreTypes.NUMBER, parameters);
        assertParameter("decimal", 5.55d, CoreTypes.DECIMAL, parameters);
        assertParameter("list_string", Arrays.asList("string 1", "string 2"), CoreTypes.LIST_OF_STRING, parameters);
    }

    private void assertParameter(String name, Object value, ParameterType<?> type, Collection<Parameter> parameters) throws Exception {
        for(Parameter parameter: parameters) {
            if(parameter.getName().equals(name)) {
                assertEquals(value, parameter.getValue());
                assertEquals(type, parameter.getType());
                return;
            }
        }

        throw new Exception("parameter not found: " + name);
    }
}
