package com.github.kxrxh.javalin.rest.database.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

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


    public AccountCredit(UUID id, UUID accountId, UUID userId, long creditLimit, BigDecimal interestRate, LocalDate dueDate, long minimumPayment, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.accountId = accountId;
        this.userId = userId;
        this.creditLimit = creditLimit;
        this.interestRate = interestRate;
        this.dueDate = dueDate;
        this.minimumPayment = minimumPayment;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


}