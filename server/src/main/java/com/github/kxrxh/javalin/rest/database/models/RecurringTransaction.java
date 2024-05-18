package com.github.kxrxh.javalin.rest.database.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecurringTransaction {

    public RecurringTransaction(UUID recurringTransactionId, UUID userId, long amount, UUID categoryId, String categoryName, String description, long frequency, UUID familyId, String familyName, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.recurringTransactionId = recurringTransactionId;
        this.userId = userId;
        this.amount = amount;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.description = description;
        this.frequency = frequency;
        this.familyId = familyId;
        this.familyName = familyName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    private UUID recurringTransactionId;
    private UUID userId;
    private long amount;
    private UUID categoryId;
    private String categoryName;
    private String description;
    private long frequency;
    private UUID familyId;
    private String familyName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastSyncedAt;
}