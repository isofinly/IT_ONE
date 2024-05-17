package com.github.kxrxh.javalin.rest.database.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExchangeRate {
    private UUID id;
    private String baseCurrency;
    private String convertedCurrency;
    private long rate;
    private LocalDate date;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}