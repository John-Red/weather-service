package com.eugene.weather.controller.data;


import java.time.LocalDate;

public record FramedSensorMetrics(String sensorId, LocalDate startDate, LocalDate endDate, WeatherMetrics metrics) {
}
