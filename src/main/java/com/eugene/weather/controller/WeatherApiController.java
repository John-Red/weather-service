package com.eugene.weather.controller;

import com.eugene.weather.data.SensorData;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/v1")
public class WeatherApiController {

    @GetMapping(path = "/{sensorId}/data/avg",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public SensorData getSensorData(@PathVariable String sensorId,
                                @RequestParam("from")
                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                @RequestParam("to")
                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return new SensorData(sensorId,startDate);
    }

    @PostMapping(path = "/update/",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public SensorData postSensorData(@RequestBody SensorData SensorData) {
        return SensorData;
    }
}
