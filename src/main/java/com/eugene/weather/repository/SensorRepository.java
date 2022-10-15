package com.eugene.weather.repository;

public interface SensorRepository {

    SensorDTO getSensorData(String sensorId);

    SensorDTO addSensorData(SensorDTO sensorDto);
}
