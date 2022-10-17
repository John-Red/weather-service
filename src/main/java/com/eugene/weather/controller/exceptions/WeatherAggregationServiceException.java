package com.eugene.weather.controller.exceptions;

public class WeatherAggregationServiceException extends RuntimeException {
    public WeatherAggregationServiceException(String message) {
        super(message);
    }
}
