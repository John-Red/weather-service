package com.eugene.weather.controller;

import com.eugene.weather.repository.SensorData;
import com.eugene.weather.service.WeatherService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<SensorData> getSensorData(@PathVariable String sensorId,
                                                    @RequestParam(value = "from", required = false)
                                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                    @RequestParam(value = "to", required = false)
                                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        startDate = setDefaultIfNull(startDate);
        endDate = setDefaultIfNull(endDate);
        return ResponseEntity.ok(weatherService.getSensorData(sensorId, startDate, endDate));
    }

    private LocalDate setDefaultIfNull(LocalDate date) {
        return date == null ? LocalDate.now() : date;
    }

    @PostMapping(path = "/data/{sensorId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SensorData> addNewSensor(@PathVariable String sensorId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(weatherService.addSensorData(sensorId));
    }

    @PutMapping(path = "/data/{sensorId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SensorData> updateSensorData(@PathVariable String sensorId,
                                                       @RequestBody SensorMetrics SensorMetrics) {
        return ResponseEntity.ok(weatherService.updateSensorData(sensorId, SensorMetrics));
    }
}
