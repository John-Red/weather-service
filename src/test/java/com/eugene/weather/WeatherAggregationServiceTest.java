package com.eugene.weather;

import com.eugene.weather.controller.DatedSensorMetrics;
import com.eugene.weather.controller.SensorMetrics;
import com.eugene.weather.repository.SensorData;
import com.eugene.weather.repository.SensorDayData;
import com.eugene.weather.repository.SensorRepository;
import com.eugene.weather.service.WeatherAggregationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class WeatherAggregationServiceTest {

    private static final LocalDate DATE = LocalDate.of(2007, 1, 1);
    private static final LocalDate NEXT_DATE = DATE.plusDays(1);

    SensorRepository sensorRepositoryMock = mock(SensorRepository.class);
    ArgumentCaptor<SensorData> argumentCaptor = ArgumentCaptor.forClass(SensorData.class);
    WeatherAggregationService sut;

    @BeforeEach
    void setUp() {
        sut = new WeatherAggregationService(sensorRepositoryMock);
    }

    @Test
    void testAddsNewSensorWithEmptyData() {
        sut.addSensorData("testId", new SensorMetrics(List.of()));

        SensorData result = captureRepositoryCall();
        assertEquals("testId", result.sensorId());
        assertEquals(0, result.datedSensorParams().size());
    }
    @Test
    void testAggregatesOneDay() {
        int temperature = 10;
        List<DatedSensorMetrics> oneMetricForDay = List.of(new DatedSensorMetrics(DATE, temperature));

        sut.addSensorData("testId", new SensorMetrics(oneMetricForDay));

        SensorData result = captureRepositoryCall();
        assertEquals("testId", result.sensorId());
        assertEquals(1, result.datedSensorParams().size());

        SensorDayData dayData = result.datedSensorParams().get(DATE.toString());
        assertEquals(10, dayData.tempAvg());
        assertEquals(10, dayData.tempSum());
        assertEquals(1, dayData.tempCount());
    }
    @Test
    void testAggregatesTwoMetricsInDay() {
        int firstTemperature = 10;
        int secondTemperature = 20;
        List<DatedSensorMetrics> twoMetricsForDay =
                List.of(new DatedSensorMetrics(DATE, firstTemperature)
                ,new DatedSensorMetrics(DATE, secondTemperature));

        sut.addSensorData("testId", new SensorMetrics(twoMetricsForDay));

        SensorData result = captureRepositoryCall();
        SensorDayData dayData = result.datedSensorParams().get(DATE.toString());
        assertEquals(15, dayData.tempAvg());
        assertEquals(30, dayData.tempSum());
        assertEquals(2, dayData.tempCount());
    }
    @Test
    void testDoesNotAggregateDifferentDates() {
        int firstTemperature = 10;
        int secondTemperature = 20;
        List<DatedSensorMetrics> twoMetricsForDifferentDays =
                List.of(new DatedSensorMetrics(DATE, firstTemperature)
                        , new DatedSensorMetrics(NEXT_DATE, secondTemperature));

        sut.addSensorData("testId", new SensorMetrics(twoMetricsForDifferentDays));

        SensorData result = captureRepositoryCall();
        SensorDayData firstDay = result.datedSensorParams().get(DATE.toString());
        assertEquals(10, firstDay.tempAvg());
        assertEquals(10, firstDay.tempSum());
        assertEquals(1, firstDay.tempCount());

        SensorDayData secondDay = result.datedSensorParams().get(NEXT_DATE.toString());
        assertEquals(20, secondDay.tempAvg());
        assertEquals(20, secondDay.tempSum());
        assertEquals(1, secondDay.tempCount());
    }


    private SensorData captureRepositoryCall() {
        verify(sensorRepositoryMock).addSensorData(argumentCaptor.capture());
        return argumentCaptor.getValue();
    }

}
