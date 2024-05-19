package com.github.kxrxh.javalin.rest.util;

import com.github.kxrxh.javalin.rest.database.models.AccountDepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AccountDepositoryTest {

    @Test
    void testNoArgsConstructor() {
        AccountDepository depository = new AccountDepository();
        assertNotNull(depository, "AccountDepository object should not be null");
    }

    @Test
    void testAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String bankName = "Bank of America";
        String accountNumber = "123456789";
        String routingNumber = "987654321";
        double interestRate = 0.02;
        long overdraftLimit = 1000L;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastSyncedAt = LocalDateTime.now();

        AccountDepository depository = new AccountDepository(id, accountId, userId, bankName, accountNumber, routingNumber, interestRate, overdraftLimit, createdAt, updatedAt, lastSyncedAt);
        assertEquals(id, depository.getId());
        assertEquals(accountId, depository.getAccountId());
        assertEquals(userId, depository.getUserId());
        assertEquals(bankName, depository.getBankName());
        assertEquals(accountNumber, depository.getAccountNumber());
        assertEquals(routingNumber, depository.getRoutingNumber());
        assertEquals(interestRate, depository.getInterestRate());
        assertEquals(overdraftLimit, depository.getOverdraftLimit());
        assertEquals(createdAt, depository.getCreatedAt());
        assertEquals(updatedAt, depository.getUpdatedAt());
        assertEquals(lastSyncedAt, depository.getLastSyncedAt());
    }

    @Test
    void testBuilder() {
        UUID id = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String bankName = "Chase";
        String accountNumber = "987654321";
        String routingNumber = "123456789";
        double interestRate = 0.03;
        long overdraftLimit = 2000L;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastSyncedAt = LocalDateTime.now();

        AccountDepository depository = AccountDepository.builder()
                .id(id)
                .accountId(accountId)
                .userId(userId)
                .bankName(bankName)
                .accountNumber(accountNumber)
                .routingNumber(routingNumber)
                .interestRate(interestRate)
                .overdraftLimit(overdraftLimit)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .lastSyncedAt(lastSyncedAt)
                .build();

        assertEquals(id, depository.getId());
        assertEquals(accountId, depository.getAccountId());
        assertEquals(userId, depository.getUserId());
        assertEquals(bankName, depository.getBankName());
        assertEquals(accountNumber, depository.getAccountNumber());
        assertEquals(routingNumber, depository.getRoutingNumber());
        assertEquals(interestRate, depository.getInterestRate());
        assertEquals(overdraftLimit, depository.getOverdraftLimit());
        assertEquals(createdAt, depository.getCreatedAt());
        assertEquals(updatedAt, depository.getUpdatedAt());
        assertEquals(lastSyncedAt, depository.getLastSyncedAt());
    }

    @Test
    void testSettersAndGetters() {
        AccountDepository depository = new AccountDepository();

        UUID id = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String bankName = "Wells Fargo";
        String accountNumber = "135792468";
        String routingNumber = "246813579";
        double interestRate = 0.04;
        long overdraftLimit = 3000L;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastSyncedAt = LocalDateTime.now();

        depository.setId(id);
        depository.setAccountId(accountId);
        depository.setUserId(userId);
        depository.setBankName(bankName);
        depository.setAccountNumber(accountNumber);
        depository.setRoutingNumber(routingNumber);
        depository.setInterestRate(interestRate);
        depository.setOverdraftLimit(overdraftLimit);
        depository.setCreatedAt(createdAt);
        depository.setUpdatedAt(updatedAt);
        depository.setLastSyncedAt(lastSyncedAt);

        assertEquals(id, depository.getId());
        assertEquals(accountId, depository.getAccountId());
        assertEquals(userId, depository.getUserId());
        assertEquals(bankName, depository.getBankName());
        assertEquals(accountNumber, depository.getAccountNumber());
        assertEquals(routingNumber, depository.getRoutingNumber());
        assertEquals(interestRate, depository.getInterestRate());
        assertEquals(overdraftLimit, depository.getOverdraftLimit());
        assertEquals(createdAt, depository.getCreatedAt());
        assertEquals(updatedAt, depository.getUpdatedAt());
        assertEquals(lastSyncedAt, depository.getLastSyncedAt());
    }

    @Test
    void testEqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String bankName = "Bank of America";
        String accountNumber = "123456789";
        String routingNumber = "987654321";
        double interestRate = 0.02;
        long overdraftLimit = 1000L;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastSyncedAt = LocalDateTime.now();

        AccountDepository depository1 = new AccountDepository(id, accountId, userId, bankName, accountNumber, routingNumber, interestRate, overdraftLimit, createdAt, updatedAt, lastSyncedAt);
        AccountDepository depository2 = new AccountDepository(id, accountId, userId, bankName, accountNumber, routingNumber, interestRate, overdraftLimit, createdAt, updatedAt, lastSyncedAt);

        assertEquals(depository1, depository2);
        assertEquals(depository1.hashCode(), depository2.hashCode());

        depository2.setInterestRate(0.03);
        assertNotEquals(depository1, depository2);
        assertNotEquals(depository1.hashCode(), depository2.hashCode());
    }

    @Test
    void testToString() {
        UUID id = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String bankName = "Bank of America";
        String accountNumber = "123456789";
        String routingNumber = "987654321";
        double interestRate = 0.02;
        long overdraftLimit = 1000L;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastSyncedAt = LocalDateTime.now();

        AccountDepository depository = new AccountDepository(id, accountId, userId, bankName, accountNumber, routingNumber, interestRate, overdraftLimit, createdAt, updatedAt, lastSyncedAt);
        String expectedString = "AccountDepository(id=" + id.toString() + ", accountId=" + accountId.toString() +
                ", userId=" + userId.toString() + ", bankName=" + bankName + ", accountNumber=" + accountNumber +
                ", routingNumber=" + routingNumber + ", interestRate=" + interestRate +
                ", overdraftLimit=" + overdraftLimit + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt +
                ", lastSyncedAt=" + lastSyncedAt + ")";

        assertEquals(expectedString, depository.toString());
    }
}
