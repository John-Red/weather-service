package com.eugene.weather.repository;

import com.eugene.weather.repository.data.SensorData;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class MongoSensorRepository implements SensorRepository {
    @Autowired
    MongoOperations mongoOperations;

    @Override
    public SensorData getSensorData(String sensorId) {
        Query query = Query.query(Criteria.where("sensorId").is(sensorId));
        return mongoOperations.findOne(query, SensorData.class);
    }

    @Override
    public SensorData addSensorData(SensorData sensorData) {
        return mongoOperations.insert(sensorData);
    }

    @Override
    public SensorData updateSensorData(SensorData sensorData) {
        Query query = Query.query(Criteria.where("sensorId").is(sensorData.sensorId()));
        Update update = Update.update("datedSensorParams", sensorData.datedSensorParams());
        return mongoOperations.findAndModify(query, update, SensorData.class, "Sensors");
    }
}
