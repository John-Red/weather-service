package com.eugene.weather;

import com.eugene.weather.controller.DatedSensorMetrics;
import com.eugene.weather.controller.SensorMetrics;
import com.eugene.weather.controller.exceptions.WeatherAggregationServiceException;
import com.eugene.weather.repository.SensorData;
import com.eugene.weather.repository.SensorDayData;
import com.eugene.weather.repository.SensorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class WeatherAggregationServiceExceptionTest {

    private static final LocalDate DATE = LocalDate.of(2007, 1, 1);
    private static final LocalDate NEXT_DATE = DATE.plusDays(1);

    SensorRepository repositoryMock = mock(SensorRepository.class);
    ArgumentCaptor<SensorData> argumentCaptor = ArgumentCaptor.forClass(SensorData.class);
    com.eugene.weather.service.WeatherAggregationService sut;

    @BeforeEach
    void setUp() {
        sut = new com.eugene.weather.service.WeatherAggregationService(repositoryMock);
    }

    @Test
    void testAddsNewSensorWithEmptyData() {
        sut.addSensorData("testId", new SensorMetrics(List.of()));

        SensorData result = captureAddRepositoryCall();
        assertEquals("testId", result.sensorId());
        assertEquals(0, result.datedSensorParams().size());
    }

    @Test
    void testAggregatesTwoMetricsInDay() {
        int firstTemperature = 10;
        int secondTemperature = 20;
        List<DatedSensorMetrics> twoMetricsForDay =
                List.of(new DatedSensorMetrics(DATE, firstTemperature)
                        , new DatedSensorMetrics(DATE, secondTemperature));

        sut.addSensorData("testId", new SensorMetrics(twoMetricsForDay));

        SensorData result = captureAddRepositoryCall();
        SensorDayData date = result.datedSensorParams().get(DATE.toString());
        assertEquals(15, date.tempAvg());
        assertEquals(30, date.tempSum());
        assertEquals(2, date.tempCount());
    }

    @Test
    void testDoesNotAggregateMetricsForDifferentDates() {
        int firstTemperature = 10;
        int secondTemperature = 20;
        List<DatedSensorMetrics> twoMetricsForDifferentDays =
                List.of(new DatedSensorMetrics(DATE, firstTemperature)
                        , new DatedSensorMetrics(NEXT_DATE, secondTemperature));

        sut.addSensorData("testId", new SensorMetrics(twoMetricsForDifferentDays));

        SensorData result = captureAddRepositoryCall();
        SensorDayData firstDay = result.datedSensorParams().get(DATE.toString());
        assertEquals(10, firstDay.tempAvg());
        assertEquals(10, firstDay.tempSum());
        assertEquals(1, firstDay.tempCount());

        SensorDayData secondDay = result.datedSensorParams().get(NEXT_DATE.toString());
        assertEquals(20, secondDay.tempAvg());
        assertEquals(20, secondDay.tempSum());
        assertEquals(1, secondDay.tempCount());
    }

    @Test
    void testUpdatesEmptyParametersWithNewMetrics() {
        int newTemperature = 10;
        String sensorId = "testId";
        List<DatedSensorMetrics> newMetrics =
                List.of(new DatedSensorMetrics(DATE, newTemperature));
        mockRepositoryGetSensorData(sensorId, Map.of());

        sut.updateSensorData(sensorId, new SensorMetrics(newMetrics));

        SensorData result = captureUpdateRepositoryCall();
        SensorDayData date = result.datedSensorParams().get(DATE.toString());
        assertEquals(10, date.tempAvg());
        assertEquals(10, date.tempSum());
        assertEquals(1, date.tempCount());
    }

    @Test
    void testReturnsOldDataWhenNewMetricsIsEmpty() {
        String sensorId = "testId";
        SensorDayData oldData = new SensorDayData(15, 30, 2);
        mockRepositoryGetSensorData(sensorId, Map.of(DATE.toString(), oldData));
        List<DatedSensorMetrics> newEmptyMetrics = List.of();

        SensorData result = sut.updateSensorData(sensorId, new SensorMetrics(newEmptyMetrics));

        SensorDayData resultData = result.datedSensorParams().get(DATE.toString());
        assertEquals(oldData, resultData);
        verify(repositoryMock, never()).updateSensorData(any());
    }

    @Test
    void testDoesNotUpdateWhenNewDataIsEmpty() {
        assertThrows(WeatherAggregationServiceException.class,
                () -> sut.updateSensorData("testId", null));

        verifyNoInteractions(repositoryMock);
    }

    private void mockRepositoryGetSensorData(String sensorId, Map<String, SensorDayData> map) {
        SensorData sensorData = new SensorData(sensorId, map);
        Mockito.when(repositoryMock.getSensorData(eq(sensorId))).thenReturn(sensorData);
    }

    private SensorData captureAddRepositoryCall() {
        verify(repositoryMock).addSensorData(argumentCaptor.capture());
        return argumentCaptor.getValue();
    }

    private SensorData captureUpdateRepositoryCall() {
        verify(repositoryMock).updateSensorData(argumentCaptor.capture());
        return argumentCaptor.getValue();
    }

}
