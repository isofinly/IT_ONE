package com.github.kxrxh.javalin.rest.util;

import com.github.kxrxh.javalin.rest.database.models.AccountCredit;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AccountCreditTest {

    @Test
    void testNoArgsConstructor() {
        AccountCredit credit = new AccountCredit();
        assertNotNull(credit, "AccountCredit object should not be null");
    }

    @Test
    void testAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        long creditLimit = 5000L;
        double interestRate = 0.1;
        LocalDate dueDate = LocalDate.now().plusMonths(1);
        long minimumPayment = 200L;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastSyncedAt = LocalDateTime.now();
        UUID userId = UUID.randomUUID();

        AccountCredit credit = new AccountCredit(id, accountId, creditLimit, interestRate, dueDate, minimumPayment, createdAt, updatedAt, lastSyncedAt, userId);
        assertEquals(id, credit.getId());
        assertEquals(accountId, credit.getAccountId());
        assertEquals(creditLimit, credit.getCreditLimit());
        assertEquals(interestRate, credit.getInterestRate());
        assertEquals(dueDate, credit.getDueDate());
        assertEquals(minimumPayment, credit.getMinimumPayment());
        assertEquals(createdAt, credit.getCreatedAt());
        assertEquals(updatedAt, credit.getUpdatedAt());
        assertEquals(lastSyncedAt, credit.getLastSyncedAt());
        assertEquals(userId, credit.getUserId());
    }

    @Test
    void testBuilder() {
        UUID id = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        long creditLimit = 10000L;
        double interestRate = 0.05;
        LocalDate dueDate = LocalDate.now().plusMonths(2);
        long minimumPayment = 300L;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastSyncedAt = LocalDateTime.now();
        UUID userId = UUID.randomUUID();

        AccountCredit credit = AccountCredit.builder()
                .id(id)
                .accountId(accountId)
                .creditLimit(creditLimit)
                .interestRate(interestRate)
                .dueDate(dueDate)
                .minimumPayment(minimumPayment)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .lastSyncedAt(lastSyncedAt)
                .userId(userId)
                .build();

        assertEquals(id, credit.getId());
        assertEquals(accountId, credit.getAccountId());
        assertEquals(creditLimit, credit.getCreditLimit());
        assertEquals(interestRate, credit.getInterestRate());
        assertEquals(dueDate, credit.getDueDate());
        assertEquals(minimumPayment, credit.getMinimumPayment());
        assertEquals(createdAt, credit.getCreatedAt());
        assertEquals(updatedAt, credit.getUpdatedAt());
        assertEquals(lastSyncedAt, credit.getLastSyncedAt());
        assertEquals(userId, credit.getUserId());
    }

    @Test
    void testSettersAndGetters() {
        AccountCredit credit = new AccountCredit();

        UUID id = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        long creditLimit = 15000L;
        double interestRate = 0.07;
        LocalDate dueDate = LocalDate.now().plusMonths(3);
        long minimumPayment = 400L;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastSyncedAt = LocalDateTime.now();
        UUID userId = UUID.randomUUID();

        credit.setId(id);
        credit.setAccountId(accountId);
        credit.setCreditLimit(creditLimit);
        credit.setInterestRate(interestRate);
        credit.setDueDate(dueDate);
        credit.setMinimumPayment(minimumPayment);
        credit.setCreatedAt(createdAt);
        credit.setUpdatedAt(updatedAt);
        credit.setLastSyncedAt(lastSyncedAt);
        credit.setUserId(userId);

        assertEquals(id, credit.getId());
        assertEquals(accountId, credit.getAccountId());
        assertEquals(creditLimit, credit.getCreditLimit());
        assertEquals(interestRate, credit.getInterestRate());
        assertEquals(dueDate, credit.getDueDate());
        assertEquals(minimumPayment, credit.getMinimumPayment());
        assertEquals(createdAt, credit.getCreatedAt());
        assertEquals(updatedAt, credit.getUpdatedAt());
        assertEquals(lastSyncedAt, credit.getLastSyncedAt());
        assertEquals(userId, credit.getUserId());
    }
}
