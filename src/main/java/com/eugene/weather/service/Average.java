package com.eugene.weather.service;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@Getter
public class Average {

    private final double avg;
    private final double sum;
    private final int count;

    public Average(double sum, int count) {
        this.sum = sum;
        this.count = count;
        this.avg = sum / count;
    }

    public Average plus(Average other) {
        return new Average(sum + other.getSum(),
                count + other.getCount());
    }
}
