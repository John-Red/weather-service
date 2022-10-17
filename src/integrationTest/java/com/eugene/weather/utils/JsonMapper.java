package com.eugene.weather.utils;

import java.util.Map;
import java.util.StringJoiner;

public class JsonMapper {
    private static final String JSON_FORMAT_PATTERN = "\"%s\": \"%s\"";

    public static String mapToJsonString(Map<String, String> parameters) {
        StringJoiner jsonJoiner = new StringJoiner(",", "{", "}");
        parameters.forEach((k, v) -> jsonJoiner.add(String.format(JSON_FORMAT_PATTERN, k, v)));
        return jsonJoiner.toString();
    }


}
