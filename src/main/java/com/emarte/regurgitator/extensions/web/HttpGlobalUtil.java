package com.emarte.regurgitator.extensions.web;

import com.emarte.regurgitator.core.*;

import java.io.IOException;
import java.util.*;

import static com.emarte.regurgitator.core.ConflictPolicy.REPLACE;
import static com.emarte.regurgitator.core.EntityLookup.parameterType;
import static com.emarte.regurgitator.extensions.web.ExtensionsWebConfigConstants.GLOBAL_METADATA_CONTEXT;

public class HttpGlobalUtil {
	private static final Log log = Log.getLog(HttpGlobalUtil.class);

	private static Map<String, Parameter> GLOBAL_PARAMETERS = new HashMap<String, Parameter>();

	public static void setGlobalParameter(String name, ParameterType type, Object value) {
		log.debug("Setting global parameter '" + name + "'");
		GLOBAL_PARAMETERS.put(name, new Parameter(new ParameterPrototype(name, type, REPLACE), type.convert(value)));
	}

	public static Parameter getGlobalParameter(String name) {
		log.debug("Retrieving global parameter '" + name + "'");
		return GLOBAL_PARAMETERS.get(name);
	}

	public static boolean removeGlobalParameter(String name) {
		log.debug("Removing global parameter '" + name + "'");
		return GLOBAL_PARAMETERS.remove(name) != null;
	}

	public static Collection<Parameter> getAllGlobalParameters() {
		log.debug("Retrieving all global parameters");
		return GLOBAL_PARAMETERS.values();
	}

	public static void addGlobalParametersFromProperties(String location, Properties properties) throws RegurgitatorException {
		log.debug("Loading global parameters from '" + location + "'");
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

	public static Message applyGlobalData(Message message) throws RegurgitatorException {
		log.debug("Adding global parameters to message");
		Parameters context = message.getContext(GLOBAL_METADATA_CONTEXT);

		for(Object id: GLOBAL_PARAMETERS.keySet()) {
			context.setValue(GLOBAL_PARAMETERS.get(id));
		}

		return message;
	}

	public static int removeAllGlobalParameters() {
		log.debug("Removing all global parameters");
		Set<String> ids = GLOBAL_PARAMETERS.keySet();

		for(Object id: ids) {
			removeGlobalParameter((String)id);
		}

		return ids.size();
	}
}
