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
public class AccountLoan {
    private UUID id;
    private UUID accountId;
    private UUID userId;
    private long loanAmount;
    private long outstandingBalance;
    private double interestRate;
    private String loanTerm;
    private LocalDate dueDate;
    private String paymentFrequency;
    private String collateral;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastSyncedAt;
}