package com.eugene.weather.service;

import com.eugene.weather.controller.data.DatedSensorMetrics;
import com.eugene.weather.controller.data.FramedSensorMetrics;
import com.eugene.weather.controller.data.SensorMetrics;
import com.eugene.weather.controller.data.WeatherMetrics;
import com.eugene.weather.controller.exceptions.SensorNotFoundException;
import com.eugene.weather.repository.SensorRepository;
import com.eugene.weather.repository.data.AverageData;
import com.eugene.weather.repository.data.SensorData;
import com.eugene.weather.repository.data.SensorDayData;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class WeatherAggregationService {
    @Autowired
    private final SensorRepository sensorRepository;

    public FramedSensorMetrics getAllSensorsData(LocalDate startDate, LocalDate endDate) {
        List<SensorData> allSensorsData = sensorRepository.getAllSensorsData();
        throwNotFoundExceptionIfNull("all", allSensorsData);
        Stream<Map.Entry<String, SensorDayData>> stream =
                allSensorsData.stream()
                        .map(SensorData::datedSensorParams)
                        .map(Map::entrySet)
                        .flatMap(Set::stream);
        WeatherMetrics weatherMetrics = calculateAverageSensorData(stream, startDate, endDate);

        return new FramedSensorMetrics("all", startDate, endDate, weatherMetrics);
    }

    public FramedSensorMetrics getSensorData(String sensorId, @NonNull LocalDate startDate, @NonNull LocalDate endDate) {
        SensorData sensorData = sensorRepository.getSensorData(sensorId);
        throwNotFoundExceptionIfNull(sensorId, sensorData);
        Stream<Map.Entry<String, SensorDayData>> stream =
                sensorData.datedSensorParams().entrySet().stream();
        WeatherMetrics weatherMetrics = calculateAverageSensorData(stream, startDate, endDate);

        return new FramedSensorMetrics(sensorId, startDate, endDate, weatherMetrics);
    }

    private WeatherMetrics calculateAverageSensorData(Stream<Map.Entry<String, SensorDayData>> stream, LocalDate startDate, LocalDate endDate) {
        return stream
                .filter(params -> LocalDate.parse(params.getKey()).isAfter(startDate))
                .filter(params -> LocalDate.parse(params.getKey()).isBefore(endDate))
                .map(Map.Entry::getValue)
                .map(this::mapToAverageMetrics)
                .reduce(AverageMetrics::plus)
                .map(am -> new WeatherMetrics(am.getTemperature().getAvg(), am.getHumidity().getAvg()))
                .orElse(new WeatherMetrics(Double.NaN, Double.NaN));
    }

    private AverageMetrics mapToAverageMetrics(SensorDayData data) {
        return new AverageMetrics(new Average(data.temperature().sum(), data.temperature().count()),
                new Average(data.humidity().sum(), data.humidity().count()));
    }

    private void throwNotFoundExceptionIfNull(String sensorId, Object sensorData) {
        if (sensorData == null) {
            throw new SensorNotFoundException(String.format("There is no sensor with id: %s", sensorId));
        }
    }

    public SensorData addSensorData(String sensorId, SensorMetrics sensorMetrics) {
        SensorData sensorData = new SensorData(sensorId, aggregateMetricsToAverageParams(sensorMetrics.sensorMetrics()));
        return sensorRepository.addSensorData(sensorData);
    }

    public SensorData updateSensorData(String sensorId, @NonNull SensorMetrics sensorMetrics) {
        SensorData oldSensorData = sensorRepository.getSensorData(sensorId);
        throwNotFoundExceptionIfNull(sensorId, oldSensorData);
        if (sensorMetrics.sensorMetrics().isEmpty()) {
            return oldSensorData;
        }
        var oldDataParams = oldSensorData.datedSensorParams();
        var newDataParams = aggregateMetricsToAverageParams(sensorMetrics.sensorMetrics());
        var datedSensorParams = mergeDayDataParameters(oldDataParams, newDataParams);
        return sensorRepository.updateSensorData(new SensorData(sensorId, datedSensorParams));
    }

    private Map<String, SensorDayData> aggregateMetricsToAverageParams(List<DatedSensorMetrics> sensorMetrics) {
        return sensorMetrics
                .stream()
                .collect(Collectors.toMap(
                        sm -> sm.date().toString(),
                        sm -> new AverageMetrics(
                                new Average(sm.temperature(), 1),
                                new Average(sm.humidity(), 1)),
                        AverageMetrics::plus))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        am -> mapToSensorDayData(am.getValue())));
    }

    private SensorDayData mapToSensorDayData(AverageMetrics averageMetrics) {
        return new SensorDayData(convertToAverageData(averageMetrics.getTemperature()),
                convertToAverageData(averageMetrics.getHumidity()));
    }


    private AverageData convertToAverageData(Average average) {
        return new AverageData(average.getAvg(), average.getSum(), average.getCount());
    }

    private Map<String, SensorDayData> mergeDayDataParameters(Map<String, SensorDayData> first, Map<String, SensorDayData> second) {
        if (first == null) {
            return second;
        }
        if (second == null) {
            return first;
        }
        HashMap<String, SensorDayData> mergeResult = new HashMap<>(first);
        for (Map.Entry<String, SensorDayData> entry : second.entrySet()) {
            if (first.containsKey(entry.getKey())) {
                SensorDayData newValue = collapseSensorData(first.get(entry.getKey()), entry.getValue());
                mergeResult.put(entry.getKey(), newValue);
            } else {
                mergeResult.put(entry.getKey(), entry.getValue());
            }
        }
        return mergeResult;
    }

    private SensorDayData collapseSensorData(SensorDayData first, SensorDayData second) {
        AverageData averageTemperature = getAverage(first.temperature(), second.temperature());
        AverageData averageHumidity = getAverage(first.humidity(), second.humidity());

        return new SensorDayData(averageTemperature, averageHumidity);
    }

    private AverageData getAverage(AverageData first, AverageData second) {
        double avg = (first.avg() + second.avg()) / 2;
        double sum = first.sum() + second.sum();
        int count = first.count() + second.count();
        return new AverageData(avg, sum, count);
    }
}
