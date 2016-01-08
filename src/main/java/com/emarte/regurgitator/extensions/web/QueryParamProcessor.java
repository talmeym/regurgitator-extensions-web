package com.emarte.regurgitator.extensions.web;

import com.emarte.regurgitator.core.*;

import java.util.*;

import static com.emarte.regurgitator.core.StringType.stringify;

public class QueryParamProcessor implements ValueProcessor {
	private static final Log log = Log.getLog(QueryParamProcessor.class);

	private final String key;

	public QueryParamProcessor(String key) {
		this.key = key;
	}

	@Override
	public Object process(Object value, Message message) throws RegurgitatorException {
		List<String> results = new ArrayList<String>();

		for(String part: stringify(value).split("&")) {
			String[] split = part.split("=");

			if(split[0].equals(key)) {
				results.add(split[1]);
			}
		}

		String result = results.size() > 0 ? stringify(results) : null;
		log.debug((result != null ? "Found '" : "Did not find '") + key + "' in query string '" + results + "'");
		return result;
	}
}
