package com.eugene.weather.controller;

import com.eugene.weather.controller.data.FramedSensorMetrics;
import com.eugene.weather.controller.data.SensorMetrics;
import com.eugene.weather.repository.data.SensorData;
import com.eugene.weather.service.WeatherAggregationService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;

@RestController
@RequestMapping("/v1")
@AllArgsConstructor
public class WeatherApiController {
    private final WeatherAggregationService weatherAggregationService;

    @GetMapping(path = "/data/{sensorId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FramedSensorMetrics> getSensorData(@PathVariable String sensorId,
                                                             @RequestParam(value = "from", required = false)
                                                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                             @RequestParam(value = "to", required = false)
                                                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        startDate = getDefaultIfNull(startDate, () -> LocalDate.EPOCH);
        endDate = getDefaultIfNull(endDate, LocalDate::now);
        return ResponseEntity.ok(weatherAggregationService.getSensorData(sensorId, startDate, endDate));
    }

    @PostMapping(path = "/data/{sensorId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SensorData> addNewSensor(@PathVariable String sensorId,
                                                   @RequestBody(required = false) SensorMetrics sensorMetrics) {
        sensorMetrics = getDefaultIfNull(sensorMetrics, () -> new SensorMetrics(List.of()));
        return ResponseEntity.status(HttpStatus.CREATED).body(weatherAggregationService.addSensorData(sensorId, sensorMetrics));
    }

    private <T> T getDefaultIfNull(T obj, Supplier<T> supplier) {
        return obj == null ? supplier.get() : obj;
    }

    @PutMapping(path = "/data/{sensorId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SensorData> updateSensorData(@PathVariable String sensorId,
                                                       @RequestBody SensorMetrics sensorMetrics) {
        return ResponseEntity.ok(weatherAggregationService.updateSensorData(sensorId, sensorMetrics));
    }
}
