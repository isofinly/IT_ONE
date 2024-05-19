package com.github.kxrxh.javalin.rest.entities;

import com.github.kxrxh.javalin.rest.database.models.Transaction;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class BudgetComparisonResult {
    private UUID transactionId;
    private String name;
    private LocalDateTime date;
    private long amount;
    private String currency;
    private UUID accountId;
    private UUID categoryId;
    private boolean excluded;
    private String notes;
    private Transaction.TransactionType transactionType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastSyncedAt;
}
