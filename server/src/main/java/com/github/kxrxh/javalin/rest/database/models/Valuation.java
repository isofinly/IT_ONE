package com.github.kxrxh.javalin.rest.database.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Valuation {
    private UUID id;
    private UUID accountId;
    private LocalDate date;
    private long value;
    private String currency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}