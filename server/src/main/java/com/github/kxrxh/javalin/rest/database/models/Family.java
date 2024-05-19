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
public class Family {
    private UUID familyId;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String currency;
}