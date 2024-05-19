package com.github.kxrxh.javalin.rest.entities;

import com.github.kxrxh.javalin.rest.database.models.Transaction;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class BudgetAnalysisResult {
    private UUID budgetId;
    private UUID userId;
    private UUID categoryId;
    private long limitAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private long alertThreshold;
    private List<Transaction> transactions;
    private long totalSpent;
    private List<BudgetComparisonResult> comparisons;
    private BudgetSuggestions suggestions;
}