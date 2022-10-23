package com.eugene.weather;

import com.eugene.weather.controller.data.DatedSensorMetrics;
import com.eugene.weather.controller.data.FramedSensorMetrics;
import com.eugene.weather.controller.data.SensorMetrics;
import com.eugene.weather.controller.exceptions.SensorNotFoundException;
import com.eugene.weather.repository.SensorRepository;
import com.eugene.weather.repository.data.AverageTemperature;
import com.eugene.weather.repository.data.SensorData;
import com.eugene.weather.repository.data.SensorDayData;
import com.eugene.weather.service.WeatherAggregationService;
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

class WeatherAggregationServiceTest {

    private static final LocalDate DATE = LocalDate.of(2007, 1, 1);
    private static final LocalDate NEXT_DATE = DATE.plusDays(1);

    SensorRepository repositoryMock = mock(SensorRepository.class);
    ArgumentCaptor<SensorData> argumentCaptor = ArgumentCaptor.forClass(SensorData.class);
    WeatherAggregationService sut;

    @BeforeEach
    void setUp() {
        sut = new WeatherAggregationService(repositoryMock);
    }

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
                List.of(new DatedSensorMetrics(DATE, firstTemperature)
                        , new DatedSensorMetrics(DATE, secondTemperature));

        sut.addSensorData("testId", new SensorMetrics(twoMetricsForDay));

        SensorData result = captureAddRepositoryCall();
        AverageTemperature date = getAverageTemperature(result, DATE);
        assertParamsEquals(15.0, 30.0, 2, date);
    }

    @Test
    void testAddDoesNotAggregateMetricsForDifferentDates() {
        int firstTemperature = 10;
        int secondTemperature = 20;
        List<DatedSensorMetrics> twoMetricsForDifferentDays =
                List.of(new DatedSensorMetrics(DATE, firstTemperature)
                        , new DatedSensorMetrics(NEXT_DATE, secondTemperature));

        sut.addSensorData("testId", new SensorMetrics(twoMetricsForDifferentDays));

        SensorData result = captureAddRepositoryCall();
        AverageTemperature firstDay = getAverageTemperature(result, DATE);
        assertParamsEquals(10.0, 10.0, 1, firstDay);

        AverageTemperature secondDay = getAverageTemperature(result, NEXT_DATE);
        assertParamsEquals(20.0, 20.0, 1, secondDay);
    }

    @Test
    void testUpdatesEmptyParametersWithNewMetrics() {
        int newTemperature = 10;
        String id = "testId";
        List<DatedSensorMetrics> newMetrics =
                List.of(new DatedSensorMetrics(DATE, newTemperature));
        mockRepositoryGetSensorData(id, Map.of());

        sut.updateSensorData(id, new SensorMetrics(newMetrics));

        SensorData result = captureUpdateRepositoryCall();
        AverageTemperature date = getAverageTemperature(result, DATE);
        assertParamsEquals(10.0, 10.0, 1, date);
    }

    @Test
    void testUpdateReturnsOldDataWhenNewMetricsIsEmpty() {
        String id = "testId";
        SensorDayData oldData = createSensorDayData(15, 30, 2);
        mockRepositoryGetSensorData(id, Map.of(DATE.toString(), oldData));
        List<DatedSensorMetrics> newEmptyMetrics = List.of();

        SensorData result = sut.updateSensorData(id, new SensorMetrics(newEmptyMetrics));

        SensorDayData resultData = result.datedSensorParams().get(DATE.toString());
        assertEquals(oldData, resultData);
        verify(repositoryMock, never()).updateSensorData(any());
    }

    @Test
    void testUpdateThrowsExceptionWhenIdDoesNotExist() {
        mockRepositoryGetSensorData(null, null);

        List<DatedSensorMetrics> newMetrics =
                List.of(new DatedSensorMetrics(DATE, 10));


        assertThrows(SensorNotFoundException.class,
                () -> sut.updateSensorData("no such id", new SensorMetrics(newMetrics)));

        verify(repositoryMock, never()).updateSensorData(any());
    }

    @Test
    void testUpdateAggregatesNewParametersWithOldOnes() {
        String id = "testId";
        SensorDayData oldTemperature = createSensorDayData(10, 10, 1);
        Map<String, SensorDayData> oldMetrics = Map.of(DATE.toString(), oldTemperature);
        mockRepositoryGetSensorData(id, oldMetrics);

        DatedSensorMetrics newTemperature = new DatedSensorMetrics(DATE, 20);
        List<DatedSensorMetrics> newMetrics = List.of(newTemperature);

        sut.updateSensorData(id, new SensorMetrics(newMetrics));

        SensorData result = captureUpdateRepositoryCall();
        AverageTemperature date = getAverageTemperature(result, DATE);
        assertParamsEquals(15.0, 30.0, 2, date);
    }

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

    private SensorDayData createSensorDayData(int avg, int sum, int count) {
        return new SensorDayData(new AverageTemperature(avg, sum, count));
    }

    private AverageTemperature getAverageTemperature(SensorData result, LocalDate date) {
        return result.datedSensorParams().get(date.toString()).temperature();
    }

    private void assertParamsEquals(double avg, double sum, int count, AverageTemperature temperature) {
        assertEquals(avg, temperature.avg());
        assertEquals(sum, temperature.sum());
        assertEquals(count, temperature.count());
    }
}
