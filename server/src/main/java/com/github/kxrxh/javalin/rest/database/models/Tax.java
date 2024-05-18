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
public class Tax {
    private UUID id;
    private String name;
    private String description;
    private long rate;
    private String currency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
