package com.eugene.weather.repository;

import java.time.LocalDate;

public interface SensorRepository {

    SensorData getSensorData(String sensorId);
    SensorData getSensorData(String sensorId, LocalDate startDate, LocalDate endDate);

    SensorData addSensorData(SensorData sensorData);

    SensorData updateSensorData(SensorData sensorData);
}
