package com.eugene.weather.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AverageMetrics {
    private Average temperature;
    private Average humidity;
    private Average wind;

    public AverageMetrics plus(AverageMetrics other) {
        return new AverageMetrics(temperature.plus(other.temperature),
                humidity.plus(other.humidity),
                wind.plus(other.getWind()));
    }
}
