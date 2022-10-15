package com.eugene.weather.service;

import com.eugene.weather.controller.SensorApiData;
import com.eugene.weather.repository.SensorDTO;
import com.eugene.weather.repository.SensorRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class WeatherService {
    @Autowired
    private final SensorRepository sensorRepository;

    public SensorDTO getSensorData(String sensorId, LocalDate startDate, LocalDate endDate) {

        return sensorRepository.getSensorData(sensorId);
    }

    public SensorDTO addSensorData(SensorApiData sensorApiData) {
        SensorDTO sensorDTO = mapToSensorDTO(sensorApiData);
        return sensorRepository.addSensorData(sensorDTO);
    }

    private SensorDTO mapToSensorDTO(SensorApiData sensorApiData) {
        SensorDTO sensorDTO = new SensorDTO();
        sensorDTO.setSensorId(sensorApiData.getSensorId());
        sensorDTO.setDate(sensorApiData.getDate());
        sensorDTO.setTemperature(sensorApiData.getTemperature());
        return sensorDTO;
    }
}
