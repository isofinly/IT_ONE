package com.github.kxrxh.javalin.rest.entities;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FinancialForecast {

    private long projectedAmount;
    private String forecastMessage;

    // Getters and setters omitted for brevity

}
