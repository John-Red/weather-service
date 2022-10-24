package com.eugene.weather.mapper;

import com.eugene.weather.controller.data.DatedSensorMetrics;
import com.eugene.weather.repository.data.AverageData;
import com.eugene.weather.repository.data.SensorData;
import com.eugene.weather.repository.data.SensorDayData;
import com.eugene.weather.service.Average;
import com.eugene.weather.service.AverageMetrics;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class MetricsMapper {

    public List<DatedSensorMetrics> mapToWeatherMetrics(SensorData updatedSensorData) {
        if (updatedSensorData == null){
            return List.of();
        }
        return updatedSensorData.datedSensorParams().entrySet()
                .stream()
                .map(entry -> new DatedSensorMetrics(
                        LocalDate.parse(entry.getKey()),
                        entry.getValue().temperature().avg(),
                        entry.getValue().humidity().avg(),
                        entry.getValue().wind().avg()))
                .toList();
    }

    public AverageMetrics mapToAverageMetrics(SensorDayData data) {
        return new AverageMetrics(new Average(data.temperature().sum(), data.temperature().count()),
                new Average(data.humidity().sum(), data.humidity().count()),
                new Average(data.wind().sum(), data.wind().count()));
    }


    public SensorDayData mapToSensorDayData(AverageMetrics averageMetrics) {
        return new SensorDayData(convertToAverageData(averageMetrics.getTemperature()),
                convertToAverageData(averageMetrics.getHumidity()),
                convertToAverageData(averageMetrics.getWind()));
    }

    private AverageData convertToAverageData(Average average) {
        return new AverageData(average.getAvg(), average.getSum(), average.getCount());
    }
}
