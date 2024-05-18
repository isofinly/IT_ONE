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
public class AccountLoan {
    private UUID id;
    private UUID accountId;
    private UUID userId;
    private long loanAmount;
    private long outstandingBalance;
    private BigDecimal interestRate;
    private String loanTerm;
    private LocalDate dueDate;
    private String paymentFrequency;
    private String collateral;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastSyncedAt;

    public AccountLoan(UUID id, UUID accountId, UUID userId, long loanAmount, long outstandingBalance, BigDecimal interestRate, String loanTerm, LocalDate dueDate, String paymentFrequency, String collateral, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.accountId = accountId;
        this.userId = userId;
        this.loanAmount = loanAmount;
        this.outstandingBalance = outstandingBalance;
        this.interestRate = interestRate;
        this.loanTerm = loanTerm;
        this.dueDate = dueDate;
        this.paymentFrequency = paymentFrequency;
        this.collateral = collateral;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


}