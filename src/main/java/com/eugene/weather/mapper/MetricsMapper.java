package com.eugene.weather.mapper;

import com.eugene.weather.repository.data.AverageData;
import com.eugene.weather.repository.data.SensorDayData;
import com.eugene.weather.service.Average;
import com.eugene.weather.service.AverageMetrics;
import org.springframework.stereotype.Component;

@Component
public class MetricsMapper {


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
