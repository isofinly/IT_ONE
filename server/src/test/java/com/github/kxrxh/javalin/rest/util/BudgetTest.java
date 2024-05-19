package com.github.kxrxh.javalin.rest.util;

import com.github.kxrxh.javalin.rest.database.models.Budget;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BudgetTest {

    @Test
    void testNoArgsConstructor() {
        Budget budget = new Budget();
        assertNotNull(budget, "Budget object should not be null");
    }

    @Test
    void testAllArgsConstructor() {
        UUID budgetId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        long limitAmount = 1000L;
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusMonths(1);
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastSyncedAt = LocalDateTime.now();
        long alertThreshold = 800L;

        Budget budget = new Budget(budgetId, userId, categoryId, limitAmount, startDate, endDate, createdAt, updatedAt, lastSyncedAt, alertThreshold);
        assertEquals(budgetId, budget.getBudgetId());
        assertEquals(userId, budget.getUserId());
        assertEquals(categoryId, budget.getCategoryId());
        assertEquals(limitAmount, budget.getLimitAmount());
        assertEquals(startDate, budget.getStartDate());
        assertEquals(endDate, budget.getEndDate());
        assertEquals(createdAt, budget.getCreatedAt());
        assertEquals(updatedAt, budget.getUpdatedAt());
        assertEquals(lastSyncedAt, budget.getLastSyncedAt());
        assertEquals(alertThreshold, budget.getAlertThreshold());
    }

    @Test
    void testBuilder() {
        UUID budgetId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        long limitAmount = 2000L;
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusMonths(1);
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastSyncedAt = LocalDateTime.now();
        long alertThreshold = 1500L;

        Budget budget = Budget.builder()
                .budgetId(budgetId)
                .userId(userId)
                .categoryId(categoryId)
                .limitAmount(limitAmount)
                .startDate(startDate)
                .endDate(endDate)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .lastSyncedAt(lastSyncedAt)
                .alertThreshold(alertThreshold)
                .build();

        assertEquals(budgetId, budget.getBudgetId());
        assertEquals(userId, budget.getUserId());
        assertEquals(categoryId, budget.getCategoryId());
        assertEquals(limitAmount, budget.getLimitAmount());
        assertEquals(startDate, budget.getStartDate());
        assertEquals(endDate, budget.getEndDate());
        assertEquals(createdAt, budget.getCreatedAt());
        assertEquals(updatedAt, budget.getUpdatedAt());
        assertEquals(lastSyncedAt, budget.getLastSyncedAt());
        assertEquals(alertThreshold, budget.getAlertThreshold());
    }

    @Test
    void testSettersAndGetters() {
        Budget budget = new Budget();

        UUID budgetId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        long limitAmount = 3000L;
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusMonths(1);
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastSyncedAt = LocalDateTime.now();
        long alertThreshold = 2500L;

        budget.setBudgetId(budgetId);
        budget.setUserId(userId);
        budget.setCategoryId(categoryId);
        budget.setLimitAmount(limitAmount);
        budget.setStartDate(startDate);
        budget.setEndDate(endDate);
        budget.setCreatedAt(createdAt);
        budget.setUpdatedAt(updatedAt);
        budget.setLastSyncedAt(lastSyncedAt);
        budget.setAlertThreshold(alertThreshold);

        assertEquals(budgetId, budget.getBudgetId());
        assertEquals(userId, budget.getUserId());
        assertEquals(categoryId, budget.getCategoryId());
        assertEquals(limitAmount, budget.getLimitAmount());
        assertEquals(startDate, budget.getStartDate());
        assertEquals(endDate, budget.getEndDate());
        assertEquals(createdAt, budget.getCreatedAt());
        assertEquals(updatedAt, budget.getUpdatedAt());
        assertEquals(lastSyncedAt, budget.getLastSyncedAt());
        assertEquals(alertThreshold, budget.getAlertThreshold());
    }
    @Test
    void testEqualsAndHashCode() {
        UUID budgetId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        long limitAmount = 1000L;
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusMonths(1);
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastSyncedAt = LocalDateTime.now();
        long alertThreshold = 800L;

        Budget budget1 = new Budget(budgetId, userId, categoryId, limitAmount, startDate, endDate, createdAt, updatedAt, lastSyncedAt, alertThreshold);
        Budget budget2 = new Budget(budgetId, userId, categoryId, limitAmount, startDate, endDate, createdAt, updatedAt, lastSyncedAt, alertThreshold);

        assertEquals(budget1, budget2);
        assertEquals(budget1.hashCode(), budget2.hashCode());

        budget2.setLimitAmount(2000L);
        Assertions.assertNotEquals(budget1, budget2);
        Assertions.assertNotEquals(budget1.hashCode(), budget2.hashCode());
    }

    @Test
    void testToString() {
        UUID budgetId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        long limitAmount = 1000L;
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusMonths(1);
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastSyncedAt = LocalDateTime.now();
        long alertThreshold = 800L;

        Budget budget = new Budget(budgetId, userId, categoryId, limitAmount, startDate, endDate, createdAt, updatedAt, lastSyncedAt, alertThreshold);
        String expectedString = "Budget(budgetId=" + budgetId.toString() + ", userId=" + userId.toString() + ", categoryId=" + categoryId.toString() +
                ", limitAmount=" + limitAmount + ", startDate=" + startDate + ", endDate=" + endDate +
                ", createdAt=" + createdAt + ", updatedAt=" + updatedAt +
                ", lastSyncedAt=" + lastSyncedAt + ", alertThreshold=" + alertThreshold + ")";

        assertEquals(expectedString, budget.toString());
    }

}
