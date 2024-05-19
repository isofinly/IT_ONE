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
public class RecurringTransaction {
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