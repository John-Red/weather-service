package com.eugene.weather.repository;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("Sensors")
public record SensorData(@Id String sensorId, List<DatedSensorData> datedSensorData) {
}
