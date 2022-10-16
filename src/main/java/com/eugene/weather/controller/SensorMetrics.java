package com.eugene.weather.controller;

import java.util.List;

public record SensorMetrics(List<DatedSensorMetrics> sensorData) {
}
