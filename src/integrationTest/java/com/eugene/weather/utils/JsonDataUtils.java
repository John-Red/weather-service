package com.eugene.weather.utils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.eugene.weather.utils.JsonMapper.mapToJsonString;

public class JsonDataUtils {
    public static String getSensorMetricsAsJsonString(Map<String, String> params) {
        return "{\"sensorMetrics\": [" + mapToJsonString(params) + "]}";
    }

    public static String getMultipleSensorMetricsAsJsonString(List<Map<String, String>> objects) {
        String jsonObjects = objects.stream()
                .map(JsonMapper::mapToJsonString)
                .collect(Collectors.joining(","));
        return "{\"sensorMetrics\": [" + jsonObjects + "]}";
    }
}
