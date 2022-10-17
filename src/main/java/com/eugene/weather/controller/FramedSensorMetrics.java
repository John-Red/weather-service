package com.eugene.weather.controller;


import java.time.LocalDate;

public record FramedSensorMetrics(String sensorId, LocalDate startDate, LocalDate endDate, WeatherMetrics metrics) {
}
