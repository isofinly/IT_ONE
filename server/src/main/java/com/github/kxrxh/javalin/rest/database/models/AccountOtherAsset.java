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
public class AccountOtherAsset {
    private UUID id;
    private UUID accountId;
    private UUID userId;
    private String assetType;
    private long purchasePrice;
    private long currentValue;
    private LocalDate purchaseDate;
    private double depreciationRate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastSyncedAt;
}