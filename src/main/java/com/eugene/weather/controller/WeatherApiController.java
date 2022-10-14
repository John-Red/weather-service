package com.eugene.weather.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/v1")
public class WeatherApiController {

    @GetMapping(path = "/{sensorId}/temperature/avg", produces = "application/json")
    public String getWeatherData(@PathVariable String sensorId,
                                 @RequestParam("from")
                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                 @RequestParam("to")
                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return sensorId + " " + startDate.toString() + " " + endDate.toString();
    }
}
