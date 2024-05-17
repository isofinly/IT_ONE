package com.github.kxrxh.javalin.rest.database.models;

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
public class User {
    private UUID userId;
    private UUID familyId;
    private String firstName;
    private String lastName;
    private String email;
    private String passwordDigest;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLogin;
    private boolean active;
    private LocalDateTime lastSyncedAt;
}