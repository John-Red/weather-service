package com.eugene.weather.controller;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record SensorApiData(
        String sensorId,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate date,
        int temperature) {
}
