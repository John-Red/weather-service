package com.eugene.weather.service;

import com.eugene.weather.controller.SensorMetrics;
import com.eugene.weather.repository.SensorData;
import com.eugene.weather.repository.SensorRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class WeatherService {
    @Autowired
    private final SensorRepository sensorRepository;

    public SensorData getSensorData(String sensorId, @NonNull LocalDate startDate, @NonNull LocalDate endDate) {

        return sensorRepository.getSensorData(sensorId, startDate, endDate);
    }

    public SensorData addSensorData(String sensorId) {
        SensorData sensorData = new SensorData(sensorId, List.of());
        return sensorRepository.addSensorData(sensorData);
    }

    public SensorData updateSensorData(String sensorId, SensorMetrics sensorMetrics) {
        return null;
    }
}
