package com.eugene.weather.repository;

import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document("Sensors")
public record SensorDTO(String sensorId, LocalDate date, int temperature) {
}
