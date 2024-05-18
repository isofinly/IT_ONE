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
public class Account {
    private UUID accountId;
    private UUID userId;
    private UUID familyId;
    private String accountType;
    private String subtype;
    private String accountName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String accountableType;
    private UUID accountableId;
    private long balance;
    private String currency;
    private boolean isActive;
    private AccountStatus status;
    private String syncWarnings;
    private String syncErrors;
    private LocalDateTime lastSyncedAt;
}
