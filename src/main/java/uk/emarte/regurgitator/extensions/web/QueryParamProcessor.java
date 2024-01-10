/*
 * Copyright (C) 2017 Miles Talmey.
 * Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
 */
package uk.emarte.regurgitator.extensions.web;

import uk.emarte.regurgitator.core.Log;
import uk.emarte.regurgitator.core.Message;
import uk.emarte.regurgitator.core.ValueProcessor;

import java.util.ArrayList;
import java.util.List;

import static uk.emarte.regurgitator.core.Log.getLog;
import static uk.emarte.regurgitator.core.StringType.stringify;

public class QueryParamProcessor implements ValueProcessor {
    private static final Log log = getLog(QueryParamProcessor.class);

    private final String key;

    public QueryParamProcessor(String key) {
        this.key = key;
    }

    @Override
    public Object process(Object value, Message message) {
        List<String> results = new ArrayList<>();

        for(String part: stringify(value).split("&")) {
            String[] split = part.split("=");

            if(split[0].equals(key)) {
                results.add(split[1]);
            }
        }

        String result = results.size() > 0 ? stringify(results) : null;
        log.debug((result != null ? "Found '" : "Did not find '") + "{}' in query string '{}'", key, results);
        return result;
    }
}
