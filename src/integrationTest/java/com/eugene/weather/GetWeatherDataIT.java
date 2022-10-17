package com.eugene.weather;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.eugene.weather.utils.JsonDataUtils.getMultipleSensorMetricsAsJsonString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class GetWeatherDataIT extends BaseSpringIT {

    @Test
    void returnIsNotFoundOnNonExistentSensorId() throws Exception {
        mockMvc.perform(get("/v1/data/do-not-exist"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("There is no sensor with id: do-not-exist"));
    }

    @Test
    void returnsSensorAverageAllDatesAsDefault() throws Exception {
        String sensorId = "Dublin-1";
        sendPostRequestToCreate(sensorId, getMultipleSensorMetricsAsJsonString(List.of(
                Map.of("date", "2000-01-01",
                        "temperature", "15"),
                Map.of("date", "2011-01-01",
                        "temperature", "25"),
                Map.of("date", "2022-01-01",
                        "temperature", "35"))));
        LocalDate today = LocalDate.now();

        mockMvc.perform(get(buildUriWith(sensorId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sensorId").value(sensorId))
                .andExpect(jsonPath("$.startDate").value("1970-01-01"))
                .andExpect(jsonPath("$.endDate").value(today.toString()))
                .andExpect(jsonPath("$.metrics").exists())
                .andExpect(jsonPath("$.metrics.temperature").value(25));
    }


    @Test
    void returnsSensorAverageForSpecificDateRange() throws Exception {
        String sensorId = "Dublin-1";
        String firstDate = "2000-01-01";
        String secondDate = "2011-01-01";
        String thirdDate = "2022-01-01";
        sendPostRequestToCreate(sensorId, getMultipleSensorMetricsAsJsonString(List.of(
                Map.of("date", firstDate,
                        "temperature", "15"),
                Map.of("date", secondDate,
                        "temperature", "25"),
                Map.of("date", thirdDate,
                        "temperature", "35"))));

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
                .andExpect(jsonPath("$.metrics.temperature").value(30.0));
    }
}
