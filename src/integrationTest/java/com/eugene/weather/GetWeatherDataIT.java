package com.eugene.weather;

import com.eugene.weather.repository.DatedSensorData;
import com.eugene.weather.repository.SensorData;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class GetWeatherDataIT extends BaseSpringIT {

    @Test
    void returnsOkWithEmptyJsonWhenSensorIsNotFound() throws Exception {
        mockMvc.perform(get("/v1/data/NON_EXISTENT_SENSOR/avg"))
                .andExpect(status().isOk());
    }

   // @Test
    void returnsSensorAverageForCurrentDateAsDefault() throws Exception {
        SensorData data = new SensorData("Dublin-1",
                List.of(new DatedSensorData(YESTERDAY, 10),
                        new DatedSensorData(TODAY, 10)));

        saveToDatabase(data);

        mockMvc.perform(get("/v1/data/Dublin-1/avg"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sensorId").value("Dublin-1"))
                .andExpect(jsonPath("$.datedSensorData.date").value(TODAY.toString()));
    }


    //@Test
    void returnsSensorAverageForSpecificDateRange() throws Exception {
        SensorData data = new SensorData("Dublin-1",
                List.of(new DatedSensorData(YESTERDAY, 10),
                        new DatedSensorData(TODAY, 20),
                        new DatedSensorData(TOMORROW, 10)));

        saveToDatabase(data);

        mockMvc.perform(get("/v1/data/Dublin-1/avg")
                        .param("from", YESTERDAY.toString())
                        .param("to", TODAY.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value(TODAY.toString()))
                .andExpect(jsonPath("$.sensorId").value("Dublin-1"))
                .andExpect(jsonPath("$.temperature").value(15));
    }

    private void saveToDatabase(SensorData... sensorData) {
        for (SensorData data : sensorData) {
            mongoTemplate.save(data);
        }
    }
}
