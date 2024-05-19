package com.github.kxrxh.javalin.rest.util;

import com.github.kxrxh.javalin.rest.database.models.Transaction;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    void testNoArgsConstructor() {
        Transaction transaction = new Transaction();
        assertNotNull(transaction, "Transaction object should not be null");
    }

    @Test
    void testAllArgsConstructor() {
        UUID transactionId = UUID.randomUUID();
        String name = "Test Transaction";
        LocalDateTime date = LocalDateTime.now();
        long amount = 1000L;
        String currency = "USD";
        UUID accountId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        boolean excluded = false;
        String notes = "Sample notes";
        Transaction.TransactionType transactionType = Transaction.TransactionType.INFLOW;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastSyncedAt = LocalDateTime.now();

        Transaction transaction = new Transaction(transactionId, name, date, amount, currency, accountId, categoryId, excluded, notes, transactionType, createdAt, updatedAt, lastSyncedAt);
        assertEquals(transactionId, transaction.getTransactionId());
        assertEquals(name, transaction.getName());
        assertEquals(date, transaction.getDate());
        assertEquals(amount, transaction.getAmount());
        assertEquals(currency, transaction.getCurrency());
        assertEquals(accountId, transaction.getAccountId());
        assertEquals(categoryId, transaction.getCategoryId());
        assertFalse(transaction.isExcluded());
        assertEquals(notes, transaction.getNotes());
        assertEquals(transactionType, transaction.getTransactionType());
        assertEquals(createdAt, transaction.getCreatedAt());
        assertEquals(updatedAt, transaction.getUpdatedAt());
        assertEquals(lastSyncedAt, transaction.getLastSyncedAt());
    }

    @Test
    void testBuilder() {
        UUID transactionId = UUID.randomUUID();
        String name = "Test Transaction";
        LocalDateTime date = LocalDateTime.now();
        long amount = 1000L;
        String currency = "USD";
        UUID accountId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        boolean excluded = true;
        String notes = "Sample notes";
        Transaction.TransactionType transactionType = Transaction.TransactionType.OUTFLOW;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastSyncedAt = LocalDateTime.now();

        Transaction transaction = Transaction.builder()
                .transactionId(transactionId)
                .name(name)
                .date(date)
                .amount(amount)
                .currency(currency)
                .accountId(accountId)
                .categoryId(categoryId)
                .excluded(excluded)
                .notes(notes)
                .transactionType(transactionType)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .lastSyncedAt(lastSyncedAt)
                .build();

        assertEquals(transactionId, transaction.getTransactionId());
        assertEquals(name, transaction.getName());
        assertEquals(date, transaction.getDate());
        assertEquals(amount, transaction.getAmount());
        assertEquals(currency, transaction.getCurrency());
        assertEquals(accountId, transaction.getAccountId());
        assertEquals(categoryId, transaction.getCategoryId());
        assertTrue(transaction.isExcluded());
        assertEquals(notes, transaction.getNotes());
        assertEquals(transactionType, transaction.getTransactionType());
        assertEquals(createdAt, transaction.getCreatedAt());
        assertEquals(updatedAt, transaction.getUpdatedAt());
        assertEquals(lastSyncedAt, transaction.getLastSyncedAt());
    }

    @Test
    void testSettersAndGetters() {
        Transaction transaction = new Transaction();

        UUID transactionId = UUID.randomUUID();
        String name = "Test Transaction";
        LocalDateTime date = LocalDateTime.now();
        long amount = 1000L;
        String currency = "USD";
        UUID accountId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        boolean excluded = false;
        String notes = "Sample notes";
        Transaction.TransactionType transactionType = Transaction.TransactionType.INFLOW;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastSyncedAt = LocalDateTime.now();

        transaction.setTransactionId(transactionId);
        transaction.setName(name);
        transaction.setDate(date);
        transaction.setAmount(amount);
        transaction.setCurrency(currency);
        transaction.setAccountId(accountId);
        transaction.setCategoryId(categoryId);
        transaction.setExcluded(excluded);
        transaction.setNotes(notes);
        transaction.setTransactionType(transactionType);
        transaction.setCreatedAt(createdAt);
        transaction.setUpdatedAt(updatedAt);
        transaction.setLastSyncedAt(lastSyncedAt);

        assertEquals(transactionId, transaction.getTransactionId());
        assertEquals(name, transaction.getName());
        assertEquals(date, transaction.getDate());
        assertEquals(amount, transaction.getAmount());
        assertEquals(currency, transaction.getCurrency());
        assertEquals(accountId, transaction.getAccountId());
        assertEquals(categoryId, transaction.getCategoryId());
        assertFalse(transaction.isExcluded());
        assertEquals(notes, transaction.getNotes());
        assertEquals(transactionType, transaction.getTransactionType());
        assertEquals(createdAt, transaction.getCreatedAt());
        assertEquals(updatedAt, transaction.getUpdatedAt());
        assertEquals(lastSyncedAt, transaction.getLastSyncedAt());
    }

    @Test
    void testToString() {
        UUID transactionId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        Transaction transaction = new Transaction(transactionId, "Test", LocalDateTime.now(), 1000L, "USD", UUID.randomUUID(), UUID.randomUUID(), false, "Notes", Transaction.TransactionType.INFLOW, createdAt, updatedAt, LocalDateTime.now());

        String expectedString = "Transaction(transactionId=" + transactionId +
                ", name=Test, date=" + transaction.getDate() +
                ", amount=1000, currency=USD, accountId=" + transaction.getAccountId() +
                ", categoryId=" + transaction.getCategoryId() + ", excluded=false, notes=Notes, " +
                "transactionType=INFLOW, createdAt=" + createdAt +
                ", updatedAt=" + updatedAt + ", lastSyncedAt=" + transaction.getLastSyncedAt() + ")";
        assertEquals(expectedString, transaction.toString());
    }
}
