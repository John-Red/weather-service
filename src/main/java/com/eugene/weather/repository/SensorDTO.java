package com.eugene.weather.repository;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document("Sensors")
@Data
public class SensorDTO {
    private String sensorId;
    private LocalDate date;
    private int temperature;
}
