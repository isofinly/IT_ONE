package com.github.kxrxh.javalin.rest.database.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    private UUID transactionId;
    private String name;
    private LocalDateTime date;
    private long amount;
    private String currency;
    private UUID accountId;
    private UUID categoryId;
    private boolean excluded;
    private String notes;
    private TransactionType transactionType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastSyncedAt;


    public enum TransactionType {
        INFLOW, OUTFLOW
    }
}
