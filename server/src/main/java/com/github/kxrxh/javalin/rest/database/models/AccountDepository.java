package com.github.kxrxh.javalin.rest.database.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDepository {
    private UUID id;
    private UUID accountId;
    private UUID userId;
    private String bankName;
    private String accountNumber;
    private String routingNumber;
    private BigDecimal interestRate;
    private long overdraftLimit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastSyncedAt;

    public AccountDepository(UUID id, UUID accountId, UUID userId, String bankName, String accountNumber, String routingNumber, BigDecimal interestRate, long overdraftLimit, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.accountId = accountId;
        this.userId = userId;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.routingNumber = routingNumber;
        this.interestRate = interestRate;
        this.overdraftLimit = overdraftLimit;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

}