package com.eugene.weather;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Map;

import static com.eugene.weather.utils.JsonDataUtils.getSensorMetricsAsJsonString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class AddWeatherDataIT extends BaseSpringIT {

    @Test
    void addsSensorDataById() throws Exception {
        mockMvc.perform(post("/v1/data/London-1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void returnsErrorWhenRecordAlreadyExists() throws Exception {
        mockMvc.perform(post("/v1/data/London-1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/v1/data/London-1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().string("Sensor already exists"));
    }

    @Test
    void returnsErrorWhenBrokenContent() throws Exception {
        String brokenMetricsWithEmptyParams = getSensorMetricsAsJsonString(Map.of());

        mockMvc.perform(post("/v1/data/London-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(brokenMetricsWithEmptyParams))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addsSensorDataWithBody() throws Exception {
        mockMvc.perform(post("/v1/data/London-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getSensorMetricsAsJsonString(Map.of(
                                "date", "2022-10-14",
                                "temperature", "20",
                                "humidity", "60",
                                "wind", "4"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].date").value("2022-10-14"))
                .andExpect(jsonPath("$[0].temperature").value("20.0"))
                .andExpect(jsonPath("$[0].humidity").value("60.0"))
                .andExpect(jsonPath("$[0].wind").value("4.0"));
    }
}
