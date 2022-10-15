package com.eugene.weather;

import com.eugene.weather.controller.SensorApiData;
import com.eugene.weather.repository.SensorDTO;
import com.eugene.weather.repository.SensorRepository;
import com.eugene.weather.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class WeatherServiceTest {

    private static final LocalDate DATE = LocalDate.of(2007, 1, 1);

    SensorRepository sensorRepositoryMock = mock(SensorRepository.class);
    ArgumentCaptor<SensorDTO> argumentCaptor = ArgumentCaptor.forClass(SensorDTO.class);
    WeatherService sut;

    @BeforeEach
    void setUp() {
        sut = new WeatherService(sensorRepositoryMock);
    }

    @Test
    void testMapsValuesCorrectly() {
        SensorApiData data = new SensorApiData("testId", DATE, 25);

        sut.addSensorData(data);

        verify(sensorRepositoryMock).addSensorData(argumentCaptor.capture());
        SensorDTO result = argumentCaptor.getValue();

        assertEquals(data.getTemperature(), result.getTemperature());
        assertEquals(data.getDate(), result.getDate());
        assertEquals(data.getSensorId(), result.getSensorId());
    }

}
