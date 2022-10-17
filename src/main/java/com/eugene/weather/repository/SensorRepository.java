package com.eugene.weather.repository;

import com.eugene.weather.repository.data.SensorData;

public interface SensorRepository {

    SensorData getSensorData(String sensorId);

    SensorData addSensorData(SensorData sensorData);

    SensorData updateSensorData(SensorData sensorData);
}
