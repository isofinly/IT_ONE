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
public class Family {
    private UUID familyId;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String currency;
}