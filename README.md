# Weather Aggregation Service

The Java service made to store aggregated metrics data for weather sensors.

## How to Run

* Clone this repository
* Make sure you are using JDK 1.17 and Gradle 7.5.x
* You can build the project and run the tests by running ```gradle build```
* You can also run integration tests by  ```gradle integrationTest```
* Once successfully built, you can run the service by ```gradle bootRun```
* By default, application starts with embedded MobgoDB
* As another option you can use ```docker-compose``` file from ```/docker``` with ```docker-compose up -d``` command.
  And run application with spring profile``` -Dspring.profiles.active=local-mongo``` to run application locally with
  predefined configuration


```
  gradle bootRun -Drun.arguments="spring.profiles.active=-Dspring.profiles.active=mongo"
```
* In case you want to use different MongoDB configuration you need to change properties in ```application-mongo.properties```

Once the application runs you should see something like this

```
2022-10-17 17:02:56.065  INFO 7736 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
2022-10-17 17:02:56.109  INFO 7736 --- [           main] com.eugene.weather.WeatherApplication    : Started WeatherApplication in 10.167 seconds (JVM running for 11.536)
```

# REST API

The REST API to the example app is described below.

**All endpoints start with its version `v1`. Breaking changes in API should be made using next version number.**

## Get average metric from all sensors

`GET /v1/data/all`

You can set time period using optional parameters `from` and `to`

**Note:** Service aggregates metrics between dates. Corner values are not included

### Request

    curl --location --request GET 'http://localhost:8080/v1/data/all?from=2022-1-1&to=2022-10-1'

### Response

    HTTP/1.1 200 OK
    Content:
    {
        "sensorId": "all",
        "startDate": "1970-01-01",
        "endDate": "2022-10-17",
        "metrics": {
            "temperature": 84.11111111111111
        }
    }

## Get average metric from sensor

`GET /v1/data/{sensorId}`

Returns aggregated metric for sensor with `sensorId`

You can set time period using optional parameters `from` and `to`

**Note:** Service aggregates metrics between dates. Corner values are not included

### Request

    curl --location --request GET 'http://localhost:8080/v1/data/Dublin-1?from=2022-1-1&to=2022-10-1'

### Response

    HTTP/1.1 200 OK
    Content:
    {
        "sensorId": "Dublin-1",
        "startDate": "2022-1-1",
        "endDate": "2022-10-1",
        "metrics": {
            "temperature": 17.5
        }
    }

## Create a new sensor

`POST /v1/data/{sensorId}`

Creates new sensor with `sensorId`

You can create sensor with metrics using optional Json object to request content:

    {
        "sensorMetrics": [{
                "date": "2022-10-14",
                "temperature": 20
            }]
    }

### Request

    curl --location --request POST 'http://localhost:8080/v1/data/London-3' --header 'Content-Type: application/json' --data-raw '{"sensorMetrics": [{"date": "2022-10-14","temperature": 20}]

### Response

    HTTP/1.1 201 Created
    Content:
    {
        "sensorId": "London-5",
        "datedSensorParams": {
            "2022-10-14": {
            "tempAvg": 20,
            "tempSum": 20,
            "tempCount": 1
            }
        }
    }

Returns `409 Conflict` in case `sensorID` already exists

### Response

    HTTP/1.1 409 Conflict
    Content:
    Sensor already exists

## Add/Update sensor metrics

`PUT /v1/data/{sensorId}`

    Content:
        {
        "sensorMetrics": [{
                "date": "2022-10-14",
                "temperature": 20
            }]
        }   

Updates metrics for sensor with `sensorId`

Adds new metrics to sensor in case metrics for this date does not exist.

In case metrics for this date exists, service will compute new average values using old and new metrics

### Request

    curl --location --request PUT 'http://localhost:8080/v1/data/London-1' --header 'Content-Type: application/json' --data-raw '{"sensorMetrics": [{"date": "2022-10-16","temperature": 0}]}'

### Response

    HTTP/1.1 200 OK
    Content:
    {
        "sensorId": "Dublin-1",
        "startDate": "2022-1-1",
        "endDate": "2022-10-1",
        "metrics": {
            "temperature": 17.5
        }
    }
