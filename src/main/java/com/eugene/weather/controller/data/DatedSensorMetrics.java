package com.eugene.weather.controller.data;

import lombok.NonNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record DatedSensorMetrics(
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @NonNull
        LocalDate date,
        int temperature) {
}
