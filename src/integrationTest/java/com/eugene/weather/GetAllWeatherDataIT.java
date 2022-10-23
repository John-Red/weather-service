package com.eugene.weather;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.eugene.weather.utils.JsonDataUtils.getMultipleSensorMetricsAsJsonString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class GetAllWeatherDataIT extends BaseSpringIT {

    @Test
    void returnIsNotFoundWhenNoSensorsInDatabase() throws Exception {
        LocalDate today = LocalDate.now();

        mockMvc.perform(get("/v1/data/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sensorId").value("all"))
                .andExpect(jsonPath("$.startDate").value("1970-01-01"))
                .andExpect(jsonPath("$.endDate").value(today.toString()))
                .andExpect(jsonPath("$.metrics").exists())
                .andExpect(jsonPath("$.metrics.temperature").value(Double.NaN))
                .andExpect(jsonPath("$.metrics.humidity").value(Double.NaN));
    }

    @Test
    void returnsSensorAverageAllDatesAsDefault() throws Exception {
        sendPostRequestToCreate("Dublin-1", getMultipleSensorMetricsAsJsonString(List.of(
                Map.of("date", "2022-01-02",
                        "temperature", "10",
                        "humidity", "50"),
                Map.of("date", "2022-01-03",
                        "temperature", "15",
                        "humidity", "60"))));

        sendPostRequestToCreate("Dublin-2", getMultipleSensorMetricsAsJsonString(List.of(
                Map.of("date", "2022-01-02",
                        "temperature", "20",
                        "humidity", "70"),
                Map.of("date", "2022-01-03",
                        "temperature", "25",
                        "humidity", "80"))));

        LocalDate today = LocalDate.now();

        mockMvc.perform(get("/v1/data/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sensorId").value("all"))
                .andExpect(jsonPath("$.startDate").value("1970-01-01"))
                .andExpect(jsonPath("$.endDate").value(today.toString()))
                .andExpect(jsonPath("$.metrics").exists())
                .andExpect(jsonPath("$.metrics.temperature").value(17.5))
                .andExpect(jsonPath("$.metrics.humidity").value(65.0));

    }


    @Test
    void returnsSensorAverageForSpecificDateRange() throws Exception {
        String sensorId = "Dublin-1";
        String firstDate = "2000-01-01";
        String secondDate = "2011-01-01";
        String thirdDate = "2022-01-01";
        sendPostRequestToCreate(sensorId, getMultipleSensorMetricsAsJsonString(List.of(
                Map.of("date", firstDate,
                        "temperature", "15",
                        "humidity", "50"),
                Map.of("date", secondDate,
                        "temperature", "25",
                        "humidity", "60"),
                Map.of("date", thirdDate,
                        "temperature", "35",
                        "humidity", "70"))));

        LocalDate from = LocalDate.parse(secondDate).minusDays(1);
        LocalDate to = LocalDate.parse(thirdDate).plusDays(1);

        mockMvc.perform(get(buildUriWith(sensorId))
                        .param("from", from.toString())
                        .param("to", to.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sensorId").value(sensorId))
                .andExpect(jsonPath("$.startDate").value(from.toString()))
                .andExpect(jsonPath("$.endDate").value(to.toString()))
                .andExpect(jsonPath("$.metrics").exists())
                .andExpect(jsonPath("$.metrics.temperature").value(30.0))
                .andExpect(jsonPath("$.metrics.humidity").value(65.0));

    }
}
