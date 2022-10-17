package com.eugene.weather.repository;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document("Sensors")
public record SensorData(@Id String sensorId, Map<String, SensorDayData> datedSensorParams) {
}
