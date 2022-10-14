package com.eugene.weather.data;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
public class SensorData {

    private final String sensorId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private final LocalDate date;
}
