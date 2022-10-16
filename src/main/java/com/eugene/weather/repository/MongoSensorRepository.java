package com.eugene.weather.repository;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
@AllArgsConstructor
public class MongoSensorRepository implements SensorRepository {
    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public SensorData getSensorData(String sensorId, LocalDate startDate, LocalDate endDate) {
        Query query = Query.query(Criteria.where("sensorId").is(sensorId));
        return mongoTemplate.findOne(query, SensorData.class);
    }

    @Override
    public SensorData addSensorData(SensorData sensorData) {
        return mongoTemplate.insert(sensorData);
    }
}
