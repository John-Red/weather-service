package com.eugene.weather.utils;

import java.util.Map;

import static com.eugene.weather.utils.JsonMapper.mapToJsonString;

public class JsonDataUtils {
    public static String getSensorMetricsAsJsonString(Map<String, String> params) {
        return "{\"sensorMetrics\": [" + mapToJsonString(params) + "]}";
    }
}
