package com.eugene.weather;

import com.eugene.weather.controller.data.DatedSensorMetrics;
import com.eugene.weather.controller.data.SensorMetrics;
import com.eugene.weather.repository.data.SensorData;
import com.eugene.weather.repository.data.SensorDayData;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddWeatherAggregationServiceTest extends BaseTest {

    @Test
    void testAddsNewSensorWithEmptyData() {
        sut.addSensorData("testId", new SensorMetrics(List.of()));

        SensorData result = captureAddRepositoryCall();
        assertEquals("testId", result.sensorId());
        assertEquals(0, result.datedSensorParams().size());
    }

    @Test
    void testAddAggregatesTwoMetricsInDay() {
        int firstTemperature = 10;
        int secondTemperature = 20;
        List<DatedSensorMetrics> twoMetricsForDay =
                List.of(new DatedSensorMetrics(DATE, firstTemperature, 0,0)
                        ,new DatedSensorMetrics(DATE, secondTemperature, 0,0));

        sut.addSensorData("testId", new SensorMetrics(twoMetricsForDay));

        SensorData result = captureAddRepositoryCall();
        SensorDayData date = getAverageTemperature(result, DATE);
        assertParamsEquals(15.0, 30.0, 2, date);
    }

    @Test
    void testAddDoesNotAggregateMetricsForDifferentDates() {
        int firstTemperature = 10;
        int secondTemperature = 20;
        List<DatedSensorMetrics> twoMetricsForDifferentDays =
                List.of(new DatedSensorMetrics(DATE, firstTemperature, 0,0)
                        , new DatedSensorMetrics(NEXT_DATE, secondTemperature, 0,0));

        sut.addSensorData("testId", new SensorMetrics(twoMetricsForDifferentDays));

        SensorData result = captureAddRepositoryCall();
        SensorDayData firstDay = getAverageTemperature(result, DATE);
        assertParamsEquals(10.0, 10.0, 1, firstDay);

        SensorDayData secondDay = getAverageTemperature(result, NEXT_DATE);
        assertParamsEquals(20.0, 20.0, 1, secondDay);
    }

}
