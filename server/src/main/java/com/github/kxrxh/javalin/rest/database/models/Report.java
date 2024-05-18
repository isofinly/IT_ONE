package com.github.kxrxh.javalin.rest.database.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Report {
    private UUID reportId;
    private UUID userId;
    private String reportType;
    private String dateRange;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Transaction> transactions;
}