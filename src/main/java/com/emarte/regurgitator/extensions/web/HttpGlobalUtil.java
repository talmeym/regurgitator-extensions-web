/*
 * Copyright (C) 2017 Miles Talmey.
 * Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
 */
package com.emarte.regurgitator.extensions.web;

import com.emarte.regurgitator.core.*;

import java.util.*;

import static com.emarte.regurgitator.core.ConflictPolicy.REPLACE;
import static com.emarte.regurgitator.core.EntityLookup.parameterType;
import static com.emarte.regurgitator.core.Log.getLog;
import static com.emarte.regurgitator.extensions.web.ExtensionsWebConfigConstants.GLOBAL_METADATA_CONTEXT;

class HttpGlobalUtil {
    private static final Log log = getLog(HttpGlobalUtil.class);
    private static final Map<String, Parameter> GLOBAL_PARAMETERS = new HashMap<String, Parameter>();

    static void setGlobalParameter(String name, ParameterType type, Object value) {
        log.debug("Setting global parameter '{}'", name);
        GLOBAL_PARAMETERS.put(name, new Parameter(new ParameterPrototype(name, type, REPLACE), type.convert(value)));
    }

    static Parameter getGlobalParameter(String name) {
        log.debug("Retrieving global parameter '{}'", name);
        return GLOBAL_PARAMETERS.get(name);
    }

    static boolean removeGlobalParameter(String name) {
        log.debug("Removing global parameter '{}'", name);
        return GLOBAL_PARAMETERS.remove(name) != null;
    }

    static Collection<Parameter> getAllGlobalParameters() {
        log.debug("Retrieving all global parameters");
        return GLOBAL_PARAMETERS.values();
    }

    @SuppressWarnings("unchecked")
    static void addGlobalParametersFromProperties(String location, Properties properties) throws RegurgitatorException {
        log.debug("Loading global parameters from '{}'", location);
        HashMap<String, String> values = new HashMap<String, String>();
        HashMap<String, ParameterType> types = new HashMap<String, ParameterType>();

        for(Enumeration<String> enumeration = (Enumeration<String>) properties.propertyNames(); enumeration.hasMoreElements(); ) {
            String entry = enumeration.nextElement();

            if(entry.contains(".") && !entry.endsWith(".type")) {
                throw new RegurgitatorException("Invalid properties entry: " + entry);
            }

            if(entry.contains(".type")) {
                int separatorIndex = entry.indexOf(".");
                String name = entry.substring(0, separatorIndex);
                String type = properties.getProperty(entry);
                types.put(name, parameterType(type));
            } else {
                String value = properties.getProperty(entry);
                values.put(entry, value);

                if(!types.containsKey(entry)) {
                    types.put(entry, CoreTypes.STRING);
                }
            }
        }

        for(String name: values.keySet()) {
            setGlobalParameter(name, types.get(name), values.get(name));
        }
    }

    static void applyGlobalData(Message message) {
        if(GLOBAL_PARAMETERS.size() > 0) {
            Parameters context = message.getContext(GLOBAL_METADATA_CONTEXT);
            log.debug("Adding global parameters to message");

            for (Object id : GLOBAL_PARAMETERS.keySet()) {
                String stringId = (String) id;
                context.setValue(GLOBAL_PARAMETERS.get(stringId));
            }
        }
    }

    static int removeAllGlobalParameters() {
        log.debug("Removing all global parameters");
        int count = GLOBAL_PARAMETERS.size();
        Iterator<Map.Entry<String, Parameter>> iterator = GLOBAL_PARAMETERS.entrySet().iterator();

        while(iterator.hasNext()) {
            Map.Entry<String, Parameter> entry = iterator.next();
            log.debug("Removing global parameter '{}'", entry.getKey());
            iterator.remove();
        }

        return count;
    }
}
