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
public class AccountInvestment {
    private UUID id;
    private UUID accountId;
    private UUID userId;
    private String investmentType;
    private long marketValue;
    private long purchasePrice;
    private LocalDate purchaseDate;
    private long dividends;
    private long interestRate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastSyncedAt;
}