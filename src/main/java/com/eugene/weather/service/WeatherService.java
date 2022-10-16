package com.eugene.weather.service;

import com.eugene.weather.controller.DatedSensorMetrics;
import com.eugene.weather.controller.SensorMetrics;
import com.eugene.weather.repository.DatedSensorData;
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

    public SensorData addSensorData(String sensorId, SensorMetrics sensorMetrics) {
        SensorData sensorData = new SensorData(sensorId, mapToDatedSensorData(sensorMetrics.sensorMetrics()));
        return sensorRepository.addSensorData(sensorData);
    }

    private List<DatedSensorData> mapToDatedSensorData(List<DatedSensorMetrics> sensorMetrics) {
        return sensorMetrics.stream()
                .map(m -> new DatedSensorData(m.date(), m.temperature()))
                .toList();
    }

    public SensorData updateSensorData(String sensorId, SensorMetrics sensorMetrics) {
        return null;
    }
}
