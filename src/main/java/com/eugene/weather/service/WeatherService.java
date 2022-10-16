package com.eugene.weather.service;

import com.eugene.weather.controller.SensorApiData;
import com.eugene.weather.repository.DatedSensorData;
import com.eugene.weather.repository.SensorDTO;
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

    public SensorDTO getSensorData(String sensorId, @NonNull LocalDate startDate, @NonNull LocalDate endDate) {

        return sensorRepository.getSensorData(sensorId, startDate, endDate);
    }

    public SensorDTO addSensorData(String sensorId) {
        SensorDTO sensorDTO = new SensorDTO(sensorId,List.of());
        return sensorRepository.addSensorData(sensorDTO);
    }

    private SensorDTO mapToSensorDTO(String sensorId,SensorApiData sensorApiData) {
        return new SensorDTO(sensorId,
                List.of(new DatedSensorData(sensorApiData.date(), sensorApiData.temperature())));
    }

    public SensorDTO updateSensorData(String sensorId, SensorApiData sensorApiData) {
        return null;
    }
}
