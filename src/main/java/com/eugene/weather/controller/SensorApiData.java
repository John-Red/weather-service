package com.eugene.weather.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
public class SensorApiData {
    private final String sensorId;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private final LocalDate date;
    private final int temperature;
}
