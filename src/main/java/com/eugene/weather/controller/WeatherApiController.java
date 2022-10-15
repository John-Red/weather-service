package com.eugene.weather.controller;

import com.eugene.weather.repository.SensorDTO;
import com.eugene.weather.service.WeatherService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/v1")
@AllArgsConstructor
public class WeatherApiController {
    private final WeatherService weatherService;

    @GetMapping(path = "/{sensorId}/data/avg",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public SensorDTO getSensorData(@PathVariable String sensorId,
                                   @RequestParam(value = "from", required = false)
                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                   @RequestParam(value = "to",required = false)
                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return weatherService.getSensorData(sensorId,startDate, endDate);
    }

    @PostMapping(path = "/add/",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public SensorDTO postSensorData(@RequestBody SensorApiData SensorApiData) {
        return weatherService.addSensorData(SensorApiData);
    }
}
