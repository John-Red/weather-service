package com.eugene.weather.repository;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@Getter
public class AverageSensorData {

    private final int tempAvg;
    private final int tempSum;
    private final int tempCount;

    public AverageSensorData(int tempSum, int tempCount) {
        this.tempSum = tempSum;
        this.tempCount = tempCount;
        this.tempAvg = tempSum / tempCount;
    }

    public AverageSensorData getAverageSum(AverageSensorData other) {
        return new AverageSensorData(tempSum + other.getTempSum(),
                tempCount + other.getTempCount());
    }

}
