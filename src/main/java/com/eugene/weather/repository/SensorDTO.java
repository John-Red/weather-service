package com.eugene.weather.repository;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document("Sensors")
public record SensorDTO(@Id String sensorId, LocalDate date, int temperature) {
}
