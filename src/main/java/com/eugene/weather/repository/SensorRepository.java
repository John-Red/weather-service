package com.eugene.weather.repository;

import java.time.LocalDate;

public interface SensorRepository {

    SensorDTO getSensorData(String sensorId, LocalDate startDate, LocalDate endDate);

    SensorDTO addSensorData(SensorDTO sensorDto);
}
