package com.eugene.weather.service;

import com.eugene.weather.controller.data.DatedSensorMetrics;
import com.eugene.weather.controller.data.FramedSensorMetrics;
import com.eugene.weather.controller.data.SensorMetrics;
import com.eugene.weather.controller.data.WeatherMetrics;
import com.eugene.weather.controller.exceptions.SensorNotFoundException;
import com.eugene.weather.repository.SensorRepository;
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
import java.util.stream.Collectors;

import static java.lang.Double.NaN;

@Service
@AllArgsConstructor
public class WeatherAggregationService {
    @Autowired
    private final SensorRepository sensorRepository;

    public FramedSensorMetrics getSensorData(String sensorId, @NonNull LocalDate startDate, @NonNull LocalDate endDate) {
        LocalDate includeStartDate = startDate.minusDays(1);
        LocalDate includeEndDate = endDate.plusDays(1);
        SensorData sensorData = sensorRepository.getSensorData(sensorId);
        double averageTemp = sensorData.datedSensorParams().entrySet()
                .stream()
                .filter(entry -> LocalDate.parse(entry.getKey()).isAfter(includeStartDate))
                .filter(entry -> LocalDate.parse(entry.getKey()).isBefore(includeEndDate))
                .map(Map.Entry::getValue)
                .mapToInt(SensorDayData::tempAvg)
                .average()
                .orElse(NaN);

        return new FramedSensorMetrics(sensorId, startDate, endDate, new WeatherMetrics(averageTemp));

    }

    public SensorData addSensorData(String sensorId, SensorMetrics sensorMetrics) {
        SensorData sensorData = new SensorData(sensorId, aggregateMetricsToAverageParams(sensorMetrics.sensorMetrics()));
        return sensorRepository.addSensorData(sensorData);
    }

    public SensorData updateSensorData(String sensorId, @NonNull SensorMetrics sensorMetrics) {
        SensorData oldSensorData = sensorRepository.getSensorData(sensorId);
        if (oldSensorData == null) {
            throw new SensorNotFoundException(String.format("There is no record with id: %s", sensorId));
        }
        if (sensorMetrics.sensorMetrics().isEmpty()) {
            return oldSensorData;
        }
        var oldDataParams = oldSensorData.datedSensorParams();
        var newDataParams = aggregateMetricsToAverageParams(sensorMetrics.sensorMetrics());
        var datedSensorParams = mergeDayDataParameters(oldDataParams, newDataParams);
        sensorRepository.updateSensorData(new SensorData(sensorId, datedSensorParams));
        return sensorRepository.getSensorData(sensorId);
    }

    private Map<String, SensorDayData> aggregateMetricsToAverageParams(List<DatedSensorMetrics> sensorMetrics) {
        return sensorMetrics
                .stream()
                .collect(Collectors.toMap(entry -> entry.date().toString(),
                        entry -> new AverageSensorData(entry.temperature(), 1),
                        AverageSensorData::getAverageSum))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        avgSensData -> mapToSensorDayData(avgSensData.getValue())));
    }

    private SensorDayData mapToSensorDayData(AverageSensorData averageSensorData) {
        return new SensorDayData(
                averageSensorData.getTempAvg(),
                averageSensorData.getTempSum(),
                averageSensorData.getTempCount());
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

    private SensorDayData collapseSensorData(SensorDayData firstVal, SensorDayData secondVal) {
        int avg = (firstVal.tempAvg() + secondVal.tempAvg()) / 2;
        int sum = firstVal.tempSum() + secondVal.tempSum();
        int count = firstVal.tempCount() + secondVal.tempCount();
        return new SensorDayData(avg, sum, count);
    }
}
