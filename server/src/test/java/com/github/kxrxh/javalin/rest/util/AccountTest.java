package com.github.kxrxh.javalin.rest.util;

import com.github.kxrxh.javalin.rest.database.models.Account;
import com.github.kxrxh.javalin.rest.database.models.AccountStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @Test
    void testNoArgsConstructor() {
        Account account = new Account();
        assertNotNull(account, "Account object should not be null");
    }

    @Test
    void testAllArgsConstructor() {
        UUID accountId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID familyId = UUID.randomUUID();
        String accountType = "Savings";
        String subtype = "Online";
        String accountName = "Online Savings Account";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        String accountableType = "User";
        UUID accountableId = UUID.randomUUID();
        long balance = 10000L;
        String currency = "USD";
        boolean isActive = true;
        AccountStatus status = AccountStatus.ACTIVE;
        String syncWarnings = "No warnings";
        String syncErrors = "No errors";
        LocalDateTime lastSyncedAt = LocalDateTime.now();

        Account account = new Account(accountId, userId, familyId, accountType, subtype, accountName, createdAt, updatedAt, accountableType, accountableId, balance, currency, isActive, status, syncWarnings, syncErrors, lastSyncedAt);
        assertEquals(accountId, account.getAccountId());
        assertEquals(userId, account.getUserId());
        assertEquals(familyId, account.getFamilyId());
        assertEquals(accountType, account.getAccountType());
        assertEquals(subtype, account.getSubtype());
        assertEquals(accountName, account.getAccountName());
        assertEquals(createdAt, account.getCreatedAt());
        assertEquals(updatedAt, account.getUpdatedAt());
        assertEquals(accountableType, account.getAccountableType());
        assertEquals(accountableId, account.getAccountableId());
        assertEquals(balance, account.getBalance());
        assertEquals(currency, account.getCurrency());
        assertTrue(account.isActive());
        assertEquals(status, account.getStatus());
        assertEquals(syncWarnings, account.getSyncWarnings());
        assertEquals(syncErrors, account.getSyncErrors());
        assertEquals(lastSyncedAt, account.getLastSyncedAt());
    }

    @Test
    void testBuilder() {
        UUID accountId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID familyId = UUID.randomUUID();
        String accountType = "Checking";
        String subtype = "Joint";
        String accountName = "Joint Checking Account";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        String accountableType = "Family";
        UUID accountableId = UUID.randomUUID();
        long balance = 20000L;
        String currency = "EUR";
        boolean isActive = false;
        AccountStatus status = AccountStatus.INACTIVE;
        String syncWarnings = "Sync warnings";
        String syncErrors = "Sync errors";
        LocalDateTime lastSyncedAt = LocalDateTime.now();

        Account account = Account.builder()
                .accountId(accountId)
                .userId(userId)
                .familyId(familyId)
                .accountType(accountType)
                .subtype(subtype)
                .accountName(accountName)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .accountableType(accountableType)
                .accountableId(accountableId)
                .balance(balance)
                .currency(currency)
                .isActive(isActive)
                .status(status)
                .syncWarnings(syncWarnings)
                .syncErrors(syncErrors)
                .lastSyncedAt(lastSyncedAt)
                .build();

        assertEquals(accountId, account.getAccountId());
        assertEquals(userId, account.getUserId());
        assertEquals(familyId, account.getFamilyId());
        assertEquals(accountType, account.getAccountType());
        assertEquals(subtype, account.getSubtype());
        assertEquals(accountName, account.getAccountName());
        assertEquals(createdAt, account.getCreatedAt());
        assertEquals(updatedAt, account.getUpdatedAt());
        assertEquals(accountableType, account.getAccountableType());
        assertEquals(accountableId, account.getAccountableId());
        assertEquals(balance, account.getBalance());
        assertEquals(currency, account.getCurrency());
        assertFalse(account.isActive());
        assertEquals(status, account.getStatus());
        assertEquals(syncWarnings, account.getSyncWarnings());
        assertEquals(syncErrors, account.getSyncErrors());
        assertEquals(lastSyncedAt, account.getLastSyncedAt());
    }

    @Test
    void testSettersAndGetters() {
        Account account = new Account();

        UUID accountId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID familyId = UUID.randomUUID();
        String accountType = "Investment";
        String subtype = "Individual";
        String accountName = "Individual Investment Account";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        String accountableType = "User";
        UUID accountableId = UUID.randomUUID();
        long balance = 30000L;
        String currency = "GBP";
        boolean isActive = true;
        AccountStatus status = AccountStatus.ACTIVE;
        String syncWarnings = "Warnings";
        String syncErrors = "Errors";
        LocalDateTime lastSyncedAt = LocalDateTime.now();

        account.setAccountId(accountId);
        account.setUserId(userId);
        account.setFamilyId(familyId);
        account.setAccountType(accountType);
        account.setSubtype(subtype);
        account.setAccountName(accountName);
        account.setCreatedAt(createdAt);
        account.setUpdatedAt(updatedAt);
        account.setAccountableType(accountableType);
        account.setAccountableId(accountableId);
        account.setBalance(balance);
        account.setCurrency(currency);
        account.setActive(isActive);
        account.setStatus(status);
        account.setSyncWarnings(syncWarnings);
        account.setSyncErrors(syncErrors);
        account.setLastSyncedAt(lastSyncedAt);

        assertEquals(accountId, account.getAccountId());
        assertEquals(userId, account.getUserId());
        assertEquals(familyId, account.getFamilyId());
        assertEquals(accountType, account.getAccountType());
        assertEquals(subtype, account.getSubtype());
        assertEquals(accountName, account.getAccountName());
        assertEquals(createdAt, account.getCreatedAt());
        assertEquals(updatedAt, account.getUpdatedAt());
        assertEquals(accountableType, account.getAccountableType());
        assertEquals(accountableId, account.getAccountableId());
        assertEquals(balance, account.getBalance());
        assertEquals(currency, account.getCurrency());
        assertTrue(account.isActive());
        assertEquals(status, account.getStatus());
        assertEquals(syncWarnings, account.getSyncWarnings());
        assertEquals(syncErrors, account.getSyncErrors());
        assertEquals(lastSyncedAt, account.getLastSyncedAt());
    }
}
