package com.eugene.weather;

import com.eugene.weather.repository.SensorDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class GetWeatherDataIT extends BaseSpringIT {

    @Test
    void returnsOkWithEmptyJsonWhenSensorIsNotFound() throws Exception {
        mockMvc.perform(get("/v1/data/NON_EXISTENT_SENSOR/avg"))
                .andExpect(status().isOk());
    }


    @Test
    void returnsSensorForCurrentDate() throws Exception {
        LocalDate currentDate = LocalDate.now();
        SensorDTO sensorDTO = new SensorDTO("Dublin-1", currentDate, 15);

        mongoTemplate.insert(sensorDTO);


        mockMvc.perform(get("/v1/data/Dublin-1/avg"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value(currentDate.toString()))
                .andExpect(jsonPath("$.sensorId").value("Dublin-1"))
                .andExpect(jsonPath("$.temperature").value("15"));
    }
}
