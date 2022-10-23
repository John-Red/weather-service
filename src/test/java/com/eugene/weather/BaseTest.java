package com.eugene.weather;

import com.eugene.weather.repository.SensorRepository;
import com.eugene.weather.repository.data.SensorData;
import com.eugene.weather.repository.data.SensorDayData;
import com.eugene.weather.service.WeatherAggregationService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public abstract class BaseTest {
    protected static final LocalDate DATE = LocalDate.of(2007, 1, 1);
    protected static final LocalDate NEXT_DATE = DATE.plusDays(1);

    SensorRepository repositoryMock = mock(SensorRepository.class);
    ArgumentCaptor<SensorData> argumentCaptor = ArgumentCaptor.forClass(SensorData.class);
    WeatherAggregationService sut;

    @BeforeEach
    protected void setUp() {
        sut = new WeatherAggregationService(repositoryMock);
    }



    protected SensorData captureAddRepositoryCall() {
        verify(repositoryMock).addSensorData(argumentCaptor.capture());
        return argumentCaptor.getValue();
    }


    protected SensorDayData getAverageTemperature(SensorData result, LocalDate date) {
        return result.datedSensorParams().get(date.toString());
    }

    protected void assertParamsEquals(double avg, double sum, int count, SensorDayData temperature) {
        assertEquals(avg, temperature.tempAvg());
        assertEquals(sum, temperature.tempSum());
        assertEquals(count, temperature.tempCount());
    }


    protected void mockRepositoryGetSensorData(String sensorId, Map<String, SensorDayData> map) {
        SensorData sensorData = new SensorData(sensorId, map);
        Mockito.when(repositoryMock.getSensorData(eq(sensorId))).thenReturn(sensorData);
    }


    protected SensorData captureUpdateRepositoryCall() {
        verify(repositoryMock).updateSensorData(argumentCaptor.capture());
        return argumentCaptor.getValue();
    }

    protected SensorDayData createSensorDayData(int avg, int sum, int count) {
        return new SensorDayData(avg, sum, count);
    }
}
