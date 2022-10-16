package com.eugene.weather.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

public record SensorApiData(
        String sensorId,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate date,
        int temperature) {
}
