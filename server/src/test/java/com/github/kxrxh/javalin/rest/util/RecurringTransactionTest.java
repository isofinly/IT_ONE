package com.github.kxrxh.javalin.rest.util;

import com.github.kxrxh.javalin.rest.database.models.RecurringTransaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RecurringTransactionTest {

    @Test
    void testNoArgsConstructor() {
        RecurringTransaction recurringTransaction = new RecurringTransaction();
        assertNotNull(recurringTransaction, "RecurringTransaction object should not be null");
    }

    @Test
    void testAllArgsConstructor() {
        UUID recurringTransactionId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        long amount = 1000L;
        UUID categoryId = UUID.randomUUID();
        String categoryName = "Groceries";
        String description = "Monthly grocery shopping";
        long frequency = 30L;
        UUID familyId = UUID.randomUUID();
        String familyName = "Smith Family";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastSyncedAt = LocalDateTime.now();

        RecurringTransaction recurringTransaction = new RecurringTransaction(recurringTransactionId, userId, amount, categoryId, categoryName, description, frequency, familyId, familyName, createdAt, updatedAt, lastSyncedAt);
        assertEquals(recurringTransactionId, recurringTransaction.getRecurringTransactionId());
        assertEquals(userId, recurringTransaction.getUserId());
        assertEquals(amount, recurringTransaction.getAmount());
        assertEquals(categoryId, recurringTransaction.getCategoryId());
        assertEquals(categoryName, recurringTransaction.getCategoryName());
        assertEquals(description, recurringTransaction.getDescription());
        assertEquals(frequency, recurringTransaction.getFrequency());
        assertEquals(familyId, recurringTransaction.getFamilyId());
        assertEquals(familyName, recurringTransaction.getFamilyName());
        assertEquals(createdAt, recurringTransaction.getCreatedAt());
        assertEquals(updatedAt, recurringTransaction.getUpdatedAt());
        assertEquals(lastSyncedAt, recurringTransaction.getLastSyncedAt());
    }

    @Test
    void testBuilder() {
        UUID recurringTransactionId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        long amount = 1500L;
        UUID categoryId = UUID.randomUUID();
        String categoryName = "Utilities";
        String description = "Monthly utility bills";
        long frequency = 30L;
        UUID familyId = UUID.randomUUID();
        String familyName = "Doe Family";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastSyncedAt = LocalDateTime.now();

        RecurringTransaction recurringTransaction = RecurringTransaction.builder()
                .recurringTransactionId(recurringTransactionId)
                .userId(userId)
                .amount(amount)
                .categoryId(categoryId)
                .categoryName(categoryName)
                .description(description)
                .frequency(frequency)
                .familyId(familyId)
                .familyName(familyName)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .lastSyncedAt(lastSyncedAt)
                .build();

        assertEquals(recurringTransactionId, recurringTransaction.getRecurringTransactionId());
        assertEquals(userId, recurringTransaction.getUserId());
        assertEquals(amount, recurringTransaction.getAmount());
        assertEquals(categoryId, recurringTransaction.getCategoryId());
        assertEquals(categoryName, recurringTransaction.getCategoryName());
        assertEquals(description, recurringTransaction.getDescription());
        assertEquals(frequency, recurringTransaction.getFrequency());
        assertEquals(familyId, recurringTransaction.getFamilyId());
        assertEquals(familyName, recurringTransaction.getFamilyName());
        assertEquals(createdAt, recurringTransaction.getCreatedAt());
        assertEquals(updatedAt, recurringTransaction.getUpdatedAt());
        assertEquals(lastSyncedAt, recurringTransaction.getLastSyncedAt());
    }

    @Test
    void testSettersAndGetters() {
        RecurringTransaction recurringTransaction = new RecurringTransaction();

        UUID recurringTransactionId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        long amount = 2000L;
        UUID categoryId = UUID.randomUUID();
        String categoryName = "Rent";
        String description = "Monthly rent payment";
        long frequency = 30L;
        UUID familyId = UUID.randomUUID();
        String familyName = "Johnson Family";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastSyncedAt = LocalDateTime.now();

        recurringTransaction.setRecurringTransactionId(recurringTransactionId);
        recurringTransaction.setUserId(userId);
        recurringTransaction.setAmount(amount);
        recurringTransaction.setCategoryId(categoryId);
        recurringTransaction.setCategoryName(categoryName);
        recurringTransaction.setDescription(description);
        recurringTransaction.setFrequency(frequency);
        recurringTransaction.setFamilyId(familyId);
        recurringTransaction.setFamilyName(familyName);
        recurringTransaction.setCreatedAt(createdAt);
        recurringTransaction.setUpdatedAt(updatedAt);
        recurringTransaction.setLastSyncedAt(lastSyncedAt);

        assertEquals(recurringTransactionId, recurringTransaction.getRecurringTransactionId());
        assertEquals(userId, recurringTransaction.getUserId());
        assertEquals(amount, recurringTransaction.getAmount());
        assertEquals(categoryId, recurringTransaction.getCategoryId());
        assertEquals(categoryName, recurringTransaction.getCategoryName());
        assertEquals(description, recurringTransaction.getDescription());
        assertEquals(frequency, recurringTransaction.getFrequency());
        assertEquals(familyId, recurringTransaction.getFamilyId());
        assertEquals(familyName, recurringTransaction.getFamilyName());
        assertEquals(createdAt, recurringTransaction.getCreatedAt());
        assertEquals(updatedAt, recurringTransaction.getUpdatedAt());
        assertEquals(lastSyncedAt, recurringTransaction.getLastSyncedAt());
    }

    @Test
    void testToString() {
        UUID recurringTransactionId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        RecurringTransaction transaction = new RecurringTransaction(recurringTransactionId, UUID.randomUUID(), 1500L, categoryId, "Utilities", "Monthly utility bills", 30L, UUID.randomUUID(), "Doe Family", createdAt, updatedAt, LocalDateTime.now());

        String expectedString = "RecurringTransaction(recurringTransactionId=" + recurringTransactionId +
                ", userId=" + transaction.getUserId() + ", amount=1500, categoryId=" + categoryId +
                ", categoryName=Utilities, description=Monthly utility bills, frequency=30" +
                ", familyId=" + transaction.getFamilyId() + ", familyName=Doe Family, createdAt=" +
                createdAt + ", updatedAt=" + updatedAt + ", lastSyncedAt=" + transaction.getLastSyncedAt() + ")";
        assertEquals(expectedString, transaction.toString());
    }
}
