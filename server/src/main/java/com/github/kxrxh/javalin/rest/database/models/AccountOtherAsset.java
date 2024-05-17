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
public class AccountOtherAsset {
    private UUID id;
    private UUID accountId;
    private String assetType;
    private long purchasePrice;
    private long currentValue;
    private LocalDate purchaseDate;
    private BigDecimal depreciationRate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastSyncedAt;
}