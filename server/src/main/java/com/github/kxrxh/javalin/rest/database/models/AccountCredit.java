package com.github.kxrxh.javalin.rest.database.models;

import java.math.BigDecimal;
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
public class AccountCredit {
    private UUID id;
    private UUID accountId;
    private long creditLimit;
    private BigDecimal interestRate;
    private LocalDate dueDate;
    private long minimumPayment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastSyncedAt;
    private UUID userId;
}