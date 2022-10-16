package com.eugene.weather.repository;

import java.time.LocalDate;

public record DatedSensorData(LocalDate date, int temperature){
}
