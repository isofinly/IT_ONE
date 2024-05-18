package com.github.kxrxh.javalin.rest.util;

import com.github.kxrxh.javalin.rest.database.models.AccountBalance;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AccountBalanceTest {

    @Test
    void testNoArgsConstructor() {
        AccountBalance balance = new AccountBalance();
        assertNotNull(balance, "AccountBalance object should not be null");
    }

    @Test
    void testAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        LocalDate date = LocalDate.now();
        long balanceAmount = 10000L;
        String currency = "USD";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        AccountBalance balance = new AccountBalance(id, accountId, date, balanceAmount, currency, createdAt, updatedAt);
        assertEquals(id, balance.getId());
        assertEquals(accountId, balance.getAccountId());
        assertEquals(date, balance.getDate());
        assertEquals(balanceAmount, balance.getBalance());
        assertEquals(currency, balance.getCurrency());
        assertEquals(createdAt, balance.getCreatedAt());
        assertEquals(updatedAt, balance.getUpdatedAt());
    }

    @Test
    void testBuilder() {
        UUID id = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        LocalDate date = LocalDate.now();
        long balanceAmount = 20000L;
        String currency = "EUR";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        AccountBalance balance = AccountBalance.builder()
                .id(id)
                .accountId(accountId)
                .date(date)
                .balance(balanceAmount)
                .currency(currency)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        assertEquals(id, balance.getId());
        assertEquals(accountId, balance.getAccountId());
        assertEquals(date, balance.getDate());
        assertEquals(balanceAmount, balance.getBalance());
        assertEquals(currency, balance.getCurrency());
        assertEquals(createdAt, balance.getCreatedAt());
        assertEquals(updatedAt, balance.getUpdatedAt());
    }

    @Test
    void testSettersAndGetters() {
        AccountBalance balance = new AccountBalance();

        UUID id = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        LocalDate date = LocalDate.now();
        long balanceAmount = 30000L;
        String currency = "GBP";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        balance.setId(id);
        balance.setAccountId(accountId);
        balance.setDate(date);
        balance.setBalance(balanceAmount);
        balance.setCurrency(currency);
        balance.setCreatedAt(createdAt);
        balance.setUpdatedAt(updatedAt);

        assertEquals(id, balance.getId());
        assertEquals(accountId, balance.getAccountId());
        assertEquals(date, balance.getDate());
        assertEquals(balanceAmount, balance.getBalance());
        assertEquals(currency, balance.getCurrency());
        assertEquals(createdAt, balance.getCreatedAt());
        assertEquals(updatedAt, balance.getUpdatedAt());
    }
}
