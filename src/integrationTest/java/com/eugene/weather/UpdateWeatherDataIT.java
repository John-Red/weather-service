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
                        .content(getSensorMetricsAsJsonString(Map.of("date", "2022-01-01"
                                , "temperature", "22"))))
                .andExpect(status().isNotFound())
                .andExpect(content().string("There is no sensor with id: do-not-exist"));
    }

    @Test
    void addsNewDataToSensor() throws Exception {
        String sensorId = "London-1";
        sendPostRequestToCreate(sensorId, getSensorMetricsAsJsonString((Map.of(
                "date", "2022-01-01",
                "temperature", "15"))));

        mockMvc.perform(put(buildUriWith(sensorId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getSensorMetricsAsJsonString(Map.of(
                                "date", "2022-01-02"
                                , "temperature", "20"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sensorId").value("London-1"))
                .andExpect(jsonPath("$.datedSensorParams.2022-01-01.tempAvg").value("15"))
                .andExpect(jsonPath("$.datedSensorParams.2022-01-02.tempAvg").value("20"));
    }


    @Test
    void updatesSensorWithNewData() throws Exception {
        String sensorId = "London-1";
        sendPostRequestToCreate(sensorId, getSensorMetricsAsJsonString(Map.of(
                "date", "2022-01-01",
                "temperature", "20")));

        mockMvc.perform(put(buildUriWith(sensorId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getSensorMetricsAsJsonString(Map.of(
                                "date", "2022-01-01"
                                , "temperature", "10"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sensorId").value("London-1"))
                .andExpect(jsonPath("$.datedSensorParams.2022-01-01.tempAvg").value("15"))
                .andExpect(jsonPath("$.datedSensorParams.2022-01-01.tempSum").value("30"))
                .andExpect(jsonPath("$.datedSensorParams.2022-01-01.tempCount").value("2"));
    }
}
