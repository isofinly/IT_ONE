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
public class AccountOtherAsset {
    private UUID id;
    private UUID accountId;
    private UUID userId;
    private String assetType;
    private long purchasePrice;
    private long currentValue;
    private LocalDate purchaseDate;
    private BigDecimal depreciationRate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastSyncedAt;

    public AccountOtherAsset(UUID id, UUID accountId, UUID userId, String assetType, long purchasePrice, long currentValue, LocalDate purchaseDate, BigDecimal depreciationRate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.accountId = accountId;
        this.userId = userId;
        this.assetType = assetType;
        this.purchasePrice = purchasePrice;
        this.currentValue = currentValue;
        this.purchaseDate = purchaseDate;
        this.depreciationRate = depreciationRate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

}