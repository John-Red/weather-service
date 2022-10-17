package com.eugene.weather.repository;

public interface SensorRepository {

    SensorData getSensorData(String sensorId);

    SensorData addSensorData(SensorData sensorData);

    SensorData updateSensorData(SensorData sensorData);
}
