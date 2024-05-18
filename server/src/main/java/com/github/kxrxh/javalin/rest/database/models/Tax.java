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
public class Tax {
    private UUID id;
    private String name;
    private String description;
    private long rate;
    private String currency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
