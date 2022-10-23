package com.eugene.weather;

import com.eugene.weather.controller.data.FramedSensorMetrics;
import com.eugene.weather.repository.data.SensorData;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GetWeatherAggregationServiceTest extends BaseTest {


    @Test
    void testGetsAverageBetweenDates() {
        String id = "testId";
        String firstDate = "2007-01-01";
        String secondDate = "2007-01-02";
        LocalDate startDate = LocalDate.parse(firstDate).minusDays(1);
        LocalDate endDate = LocalDate.parse(secondDate).plusDays(1);
        mockRepositoryGetSensorData(id,
                Map.of(firstDate, createSensorDayData(10, 1, 1),
                        secondDate, createSensorDayData(20, 2, 1)));


        FramedSensorMetrics result = sut.getSensorData(id, startDate, endDate);

        assertEquals("testId", result.sensorId());
        assertEquals(15.0, result.metrics().temperature());
    }

    @Test
    void testGetReturnsNanWhenStartDateIsBiggerThatEndDate() {
        String id = "testId";
        LocalDate startDate = LocalDate.parse("2007-01-01");
        LocalDate endDate = startDate.minusDays(1);
        mockRepositoryGetSensorData(id,
                Map.of(startDate.toString(), createSensorDayData(10, 1, 1),
                        endDate.toString(), createSensorDayData(20, 2, 1)));


        FramedSensorMetrics result = sut.getSensorData(id, startDate, endDate);

        assertEquals(Double.NaN, result.metrics().temperature());
    }

    @Test
    void testGetReturnsNanWhenThereIsNoData() {
        String id = "testId";
        LocalDate startDate = LocalDate.parse("2007-01-01");
        LocalDate endDate = LocalDate.parse("2007-01-02");
        mockRepositoryGetSensorData(id, Map.of());


        FramedSensorMetrics result = sut.getSensorData(id, startDate, endDate);

        assertEquals(Double.NaN, result.metrics().temperature());
    }

    @Test
    void testGetDoesNotIncludeDatesOutLimit() {
        String id = "testId";
        String firstDate = "2007-01-01";
        String secondDate = "2007-01-02";
        String thirdDate = "2007-01-03";
        LocalDate startDate = LocalDate.parse(firstDate).minusDays(1);
        LocalDate endDate = LocalDate.parse(secondDate).plusDays(1);
        mockRepositoryGetSensorData(id,
                Map.of(firstDate, createSensorDayData(10, 10, 1),
                        secondDate, createSensorDayData(20, 20, 1),
                        thirdDate, createSensorDayData(30, 30, 1)));


        FramedSensorMetrics result = sut.getSensorData(id, startDate, endDate);

        assertEquals(15.0, result.metrics().temperature());
        assertEquals(startDate, result.startDate());
        assertEquals(endDate, result.endDate());
    }

    @Test
    void testGetAllDataDoesNotIncludeDatesOutLimit() {
        String firstDate = "2007-01-01";
        String secondDate = "2007-01-02";
        String thirdDate = "2007-01-03";

        Mockito.when(repositoryMock.getAllSensorsData())
                .thenReturn(List.of(
                        new SensorData("firstSensor", Map.of(
                                firstDate, createSensorDayData(5, 10, 1),
                                secondDate, createSensorDayData(15, 20, 1),
                                thirdDate, createSensorDayData(30, 30, 1)
                        )),
                        new SensorData("secondSensor", Map.of(
                                firstDate, createSensorDayData(20, 10, 1),
                                secondDate, createSensorDayData(30, 20, 1),
                                thirdDate, createSensorDayData(40, 40, 1)
                        ))));

        LocalDate startDate = LocalDate.parse(secondDate).minusDays(1);
        LocalDate endDate = LocalDate.parse(thirdDate).plusDays(1);

        FramedSensorMetrics result = sut.getAllSensorsData(startDate, endDate);

        assertEquals(28.75, result.metrics().temperature());
        assertEquals(startDate, result.startDate());
        assertEquals(endDate, result.endDate());
    }

    @Test
    void testAllDataGetsAverageBetweenDates() {
        String firstDate = "2007-01-01";
        String secondDate = "2007-01-02";

        Mockito.when(repositoryMock.getAllSensorsData())
                .thenReturn(List.of(
                        new SensorData("firstSensor", Map.of(
                                firstDate, createSensorDayData(5, 10, 1),
                                secondDate, createSensorDayData(15, 20, 1)
                        )),
                        new SensorData("secondSensor", Map.of(
                                firstDate, createSensorDayData(20, 10, 1),
                                secondDate, createSensorDayData(30, 20, 1)
                        ))));

        LocalDate startDate = LocalDate.parse(firstDate).minusDays(1);
        LocalDate endDate = LocalDate.parse(secondDate).plusDays(1);

        FramedSensorMetrics result = sut.getAllSensorsData(startDate, endDate);

        assertEquals("all", result.sensorId());
        assertEquals(17.5, result.metrics().temperature());
    }

    @Test
    void testGetAllReturnsNanWhenStartDateIsBiggerThatEndDate() {
        LocalDate startDate = LocalDate.parse("2007-01-01");
        LocalDate endDate = startDate.minusDays(1);
        Mockito.when(repositoryMock.getAllSensorsData())
                .thenReturn(List.of(
                        new SensorData("firstSensor", Map.of(
                                startDate.toString(), createSensorDayData(5, 10, 1)))));


        FramedSensorMetrics result = sut.getAllSensorsData(startDate, endDate);

        assertEquals(Double.NaN, result.metrics().temperature());
    }
}
