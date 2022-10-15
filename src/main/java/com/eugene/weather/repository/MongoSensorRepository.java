package com.eugene.weather.repository;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class MongoSensorRepository implements SensorRepository {
    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public SensorDTO getSensorData(String sensorId) {
        Query query = Query.query(Criteria.where("sensorId").is(sensorId));
        return mongoTemplate.findOne(query, SensorDTO.class);
    }

    @Override
    public SensorDTO addSensorData(SensorDTO sensorData) {
        return mongoTemplate.insert(sensorData);
    }
}
