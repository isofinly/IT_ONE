package com.github.kxrxh.javalin.rest.database.models;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecurringTransaction {
    private UUID recurringTransactionId;
    private UUID userId;
    private long amount;
    private UUID categoryId;
    private String description;
    private long frequency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastSyncedAt;
}