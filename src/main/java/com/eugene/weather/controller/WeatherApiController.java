package com.eugene.weather.controller;

import com.eugene.weather.repository.SensorDTO;
import com.eugene.weather.service.WeatherService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/v1")
@AllArgsConstructor
public class WeatherApiController {
    private final WeatherService weatherService;

    @GetMapping(path = "/data/{sensorId}/avg",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SensorDTO> getSensorData(@PathVariable String sensorId,
                                                   @RequestParam(value = "from", required = false)
                                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                   @RequestParam(value = "to", required = false)
                                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        if (startDate == null) {
            startDate = LocalDate.now();
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        return ResponseEntity.ok(weatherService.getSensorData(sensorId, startDate, endDate));
    }

    @PostMapping(path = "/data/add/",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SensorDTO> postSensorData(@RequestBody SensorApiData SensorApiData) {
        return ResponseEntity.ok(weatherService.addSensorData(SensorApiData));
    }
}
