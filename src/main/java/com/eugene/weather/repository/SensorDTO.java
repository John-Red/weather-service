package com.eugene.weather.repository;

import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document("Sensors")
@AllArgsConstructor
public class SensorDTO {
    private String sensorId;
    private LocalDate date;
}
