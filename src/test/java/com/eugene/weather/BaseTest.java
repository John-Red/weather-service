package com.eugene.weather;

import com.eugene.weather.mapper.MetricsMapper;
import com.eugene.weather.repository.SensorRepository;
import com.eugene.weather.repository.data.AverageData;
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
    MetricsMapper mapper = new MetricsMapper();
    WeatherAggregationService sut;

    @BeforeEach
    protected void setUp() {
        sut = new WeatherAggregationService(repositoryMock, mapper);
    }


    protected SensorData captureAddRepositoryCall() {
        verify(repositoryMock).addSensorData(argumentCaptor.capture());
        return argumentCaptor.getValue();
    }


    protected SensorDayData getAverageTemperature(SensorData result, LocalDate date) {
        return result.datedSensorParams().get(date.toString());
    }

    protected void assertParamsEquals(double avg, double sum, int count, SensorDayData sensorDayData) {
        AverageData temperature = sensorDayData.temperature();
        assertEquals(avg, temperature.avg());
        assertEquals(sum, temperature.sum());
        assertEquals(count, temperature.count());
    }


    protected void mockRepositoryGetSensorData(String sensorId, Map<String, SensorDayData> map) {
        SensorData sensorData = new SensorData(sensorId, map);
        Mockito.when(repositoryMock.getSensorData(eq(sensorId))).thenReturn(sensorData);
    }


    protected SensorData captureUpdateRepositoryCall() {
        verify(repositoryMock).updateSensorData(argumentCaptor.capture());
        return argumentCaptor.getValue();
    }

    protected SensorDayData createSensorDayData(double sum, int count) {
        double avg = sum / count;
        return new SensorDayData(new AverageData(avg, sum, count),
                new AverageData(avg, sum, count));
    }
}
