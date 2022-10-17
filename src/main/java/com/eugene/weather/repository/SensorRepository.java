package com.eugene.weather.repository;

import com.eugene.weather.repository.data.SensorData;

import java.util.List;

public interface SensorRepository {

    List<SensorData> getAllSensorsData();

    SensorData getSensorData(String sensorId);

    SensorData addSensorData(SensorData sensorData);

    SensorData updateSensorData(SensorData sensorData);
}
