package com.eugene.weather;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Map;

import static com.eugene.weather.utils.JsonDataUtils.getSensorMetricsAsJsonString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class UpdateWeatherDataIT extends BaseSpringIT {

    @Test
    void returnsBadRequestWhenNoContent() throws Exception {
        mockMvc.perform(put("/v1/data/London-1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returnsNotFoundWhenThereIsNoSensorIdInDatabase() throws Exception {
        mockMvc.perform(put("/v1/data/do-not-exist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getSensorMetricsAsJsonString(Map.of("date", "2022-01-01",
                                "temperature", "22",
                                "humidity", "60",
                                "wind", "4"))))
                .andExpect(status().isNotFound())
                .andExpect(content().string("There is no sensor with id: do-not-exist"));
    }

    @Test
    void addsNewDataToSensor() throws Exception {
        String sensorId = "London-1";
        sendPostRequestToCreate(sensorId, getSensorMetricsAsJsonString((Map.of(
                "date", "2022-01-01",
                "temperature", "15",
                "humidity", "50",
                "wind", "0"))));

        mockMvc.perform(put(buildUriWith(sensorId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getSensorMetricsAsJsonString(Map.of(
                                "date", "2022-01-02",
                                "temperature", "20",
                                "humidity", "60",
                                "wind", "4"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].date").value("2022-01-01"))
                .andExpect(jsonPath("$[0].temperature").value("15.0"))
                .andExpect(jsonPath("$[0].humidity").value("50.0"))
                .andExpect(jsonPath("$[0].wind").value("0.0"))
                .andExpect(jsonPath("$[1].date").value("2022-01-02"))
                .andExpect(jsonPath("$[1].temperature").value("20.0"))
                .andExpect(jsonPath("$[1].humidity").value("60.0"))
                .andExpect(jsonPath("$[1].wind").value("4.0"));

    }


    @Test
    void updatesSensorWithNewData() throws Exception {
        String sensorId = "London-1";
        sendPostRequestToCreate(sensorId, getSensorMetricsAsJsonString(Map.of(
                "date", "2022-01-01",
                "temperature", "20",
                "humidity", "50",
                "wind", "4")));

        mockMvc.perform(put(buildUriWith(sensorId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getSensorMetricsAsJsonString(Map.of(
                                "date", "2022-01-01",
                                "temperature", "10",
                                "humidity", "60",
                                "wind", "6"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].date").value("2022-01-01"))
                .andExpect(jsonPath("$[0].temperature").value("15.0"))
                .andExpect(jsonPath("$[0].humidity").value("55.0"))
                .andExpect(jsonPath("$[0].wind").value("5.0"));
    }
}
