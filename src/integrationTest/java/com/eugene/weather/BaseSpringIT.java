package com.eugene.weather;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class BaseSpringIT {
    protected static final LocalDate YESTERDAY = LocalDate.now().minus(1, ChronoUnit.DAYS);
    protected static final LocalDate TODAY = LocalDate.now();
    protected static final LocalDate TOMORROW = LocalDate.now().plus(1, ChronoUnit.DAYS);
    private static final Query FIND_ALL = Query.query(Criteria.where("sensorId").exists(true));
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected MongoTemplate mongoTemplate;

    @AfterEach
    void tearDown() {
//        mongoTemplate.remove(FIND_ALL, "Sensors");
        mongoTemplate.getDb().drop();
    }
}
