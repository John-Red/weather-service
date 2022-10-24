package com.eugene.weather.repository.data;

import lombok.Builder;

@Builder
public record SensorDayData(AverageData temperature, AverageData humidity) {
}
