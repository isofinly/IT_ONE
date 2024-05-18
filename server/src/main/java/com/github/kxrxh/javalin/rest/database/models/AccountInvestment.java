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
public class AccountInvestment {
    private UUID id;
    private UUID accountId;
    private UUID userId;
    private String investmentType;
    private long marketValue;
    private long purchasePrice;
    private LocalDate purchaseDate;
    private long dividends;
    private BigDecimal interestRate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastSyncedAt;

    public AccountInvestment(UUID id, UUID accountId, UUID userId, String investmentType, long marketValue, long purchasePrice, LocalDate purchaseDate, long dividends, BigDecimal interestRate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.accountId = accountId;
        this.userId = userId;
        this.investmentType = investmentType;
        this.marketValue = marketValue;
        this.purchasePrice = purchasePrice;
        this.purchaseDate = purchaseDate;
        this.dividends = dividends;
        this.interestRate = interestRate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}