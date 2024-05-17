package com.github.kxrxh.javalin.rest.database.models;

import java.math.BigDecimal;
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
public class AccountDepository {
    private UUID id;
    private UUID accountId;
    private String bankName;
    private String accountNumber;
    private String routingNumber;
    private BigDecimal interestRate;
    private long overdraftLimit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastSyncedAt;
}