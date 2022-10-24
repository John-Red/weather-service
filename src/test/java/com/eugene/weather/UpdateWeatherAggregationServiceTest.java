package com.eugene.weather;

import com.eugene.weather.controller.data.DatedSensorMetrics;
import com.eugene.weather.controller.data.SensorMetrics;
import com.eugene.weather.controller.exceptions.SensorNotFoundException;
import com.eugene.weather.repository.data.SensorData;
import com.eugene.weather.repository.data.SensorDayData;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UpdateWeatherAggregationServiceTest extends BaseTest {


    @Test
    void testUpdatesEmptyParametersWithNewMetrics() {
        double newTemperature = 10;
        String id = "testId";
        List<DatedSensorMetrics> newMetrics =
                List.of(new DatedSensorMetrics(DATE, newTemperature, 0.0,0.0));
        mockRepositoryGetSensorData(id, Map.of());

        sut.updateSensorData(id, new SensorMetrics(newMetrics));

        SensorData result = captureUpdateRepositoryCall();
        SensorDayData date = getAverageTemperature(result, DATE);
        assertParamsEquals(10.0, 10.0, 1, date);
    }

    @Test
    void testUpdateReturnsOldDataWhenNewMetricsIsEmpty() {
        String id = "testId";
        SensorDayData oldData = createSensorDayData(30, 2);
        mockRepositoryGetSensorData(id, Map.of(DATE.toString(), oldData));
        List<DatedSensorMetrics> newEmptyMetrics = List.of();

        List<DatedSensorMetrics> result = sut.updateSensorData(id, new SensorMetrics(newEmptyMetrics));

        assertEquals(1,result.size());
        DatedSensorMetrics resultData = result.get(0);
        assertEquals(oldData.temperature().avg(), resultData.temperature());
        assertEquals(oldData.humidity().avg(), resultData.humidity());
        assertEquals(oldData.wind().avg(), resultData.wind());
        verify(repositoryMock, never()).updateSensorData(any());
    }

    @Test
    void testUpdateThrowsExceptionWhenIdDoesNotExist() {
        mockRepositoryGetSensorData(null, null);

        List<DatedSensorMetrics> newMetrics =
                List.of(new DatedSensorMetrics(DATE, 10.0, 0.0,0.0));


        assertThrows(SensorNotFoundException.class,
                () -> sut.updateSensorData("no such id", new SensorMetrics(newMetrics)));

        verify(repositoryMock, never()).updateSensorData(any());
    }

    @Test
    void testUpdateAggregatesNewParametersWithOldOnes() {
        String id = "testId";
        SensorDayData oldTemperature = createSensorDayData(10, 1);
        Map<String, SensorDayData> oldMetrics = Map.of(DATE.toString(), oldTemperature);
        mockRepositoryGetSensorData(id, oldMetrics);

        DatedSensorMetrics newTemperature = new DatedSensorMetrics(DATE, 20.0, 0.0,0.0);
        List<DatedSensorMetrics> newMetrics = List.of(newTemperature);

        sut.updateSensorData(id, new SensorMetrics(newMetrics));

        SensorData result = captureUpdateRepositoryCall();
        SensorDayData date = getAverageTemperature(result, DATE);
        assertParamsEquals(15.0, 30.0, 2, date);
    }
}
