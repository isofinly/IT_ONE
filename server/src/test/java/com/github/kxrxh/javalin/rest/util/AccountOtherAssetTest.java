package com.github.kxrxh.javalin.rest.util;

import com.github.kxrxh.javalin.rest.database.models.AccountOtherAsset;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AccountOtherAssetTest {

    @Test
    void testNoArgsConstructor() {
        AccountOtherAsset asset = new AccountOtherAsset();
        assertNotNull(asset, "AccountOtherAsset object should not be null");
    }

    @Test
    void testAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String assetType = "Stocks";
        long purchasePrice = 10000L;
        long currentValue = 12000L;
        LocalDate purchaseDate = LocalDate.now();
        double depreciationRate = 0.1;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastSyncedAt = LocalDateTime.now();

        AccountOtherAsset asset = new AccountOtherAsset(id, accountId, userId, assetType, purchasePrice, currentValue, purchaseDate, depreciationRate, createdAt, updatedAt, lastSyncedAt);
        assertEquals(id, asset.getId());
        assertEquals(accountId, asset.getAccountId());
        assertEquals(userId, asset.getUserId());
        assertEquals(assetType, asset.getAssetType());
        assertEquals(purchasePrice, asset.getPurchasePrice());
        assertEquals(currentValue, asset.getCurrentValue());
        assertEquals(purchaseDate, asset.getPurchaseDate());
        assertEquals(depreciationRate, asset.getDepreciationRate());
        assertEquals(createdAt, asset.getCreatedAt());
        assertEquals(updatedAt, asset.getUpdatedAt());
        assertEquals(lastSyncedAt, asset.getLastSyncedAt());
    }

    @Test
    void testBuilder() {
        UUID id = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String assetType = "Real Estate";
        long purchasePrice = 200000L;
        long currentValue = 250000L;
        LocalDate purchaseDate = LocalDate.now();
        double depreciationRate = 0.05;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastSyncedAt = LocalDateTime.now();

        AccountOtherAsset asset = AccountOtherAsset.builder()
                .id(id)
                .accountId(accountId)
                .userId(userId)
                .assetType(assetType)
                .purchasePrice(purchasePrice)
                .currentValue(currentValue)
                .purchaseDate(purchaseDate)
                .depreciationRate(depreciationRate)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .lastSyncedAt(lastSyncedAt)
                .build();

        assertEquals(id, asset.getId());
        assertEquals(accountId, asset.getAccountId());
        assertEquals(userId, asset.getUserId());
        assertEquals(assetType, asset.getAssetType());
        assertEquals(purchasePrice, asset.getPurchasePrice());
        assertEquals(currentValue, asset.getCurrentValue());
        assertEquals(purchaseDate, asset.getPurchaseDate());
        assertEquals(depreciationRate, asset.getDepreciationRate());
        assertEquals(createdAt, asset.getCreatedAt());
        assertEquals(updatedAt, asset.getUpdatedAt());
        assertEquals(lastSyncedAt, asset.getLastSyncedAt());
    }

    @Test
    void testSettersAndGetters() {
        AccountOtherAsset asset = new AccountOtherAsset();

        UUID id = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String assetType = "Cryptocurrency";
        long purchasePrice = 5000L;
        long currentValue = 6000L;
        LocalDate purchaseDate = LocalDate.now();
        double depreciationRate = 0.02;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastSyncedAt = LocalDateTime.now();

        asset.setId(id);
        asset.setAccountId(accountId);
        asset.setUserId(userId);
        asset.setAssetType(assetType);
        asset.setPurchasePrice(purchasePrice);
        asset.setCurrentValue(currentValue);
        asset.setPurchaseDate(purchaseDate);
        asset.setDepreciationRate(depreciationRate);
        asset.setCreatedAt(createdAt);
        asset.setUpdatedAt(updatedAt);
        asset.setLastSyncedAt(lastSyncedAt);

        assertEquals(id, asset.getId());
        assertEquals(accountId, asset.getAccountId());
        assertEquals(userId, asset.getUserId());
        assertEquals(assetType, asset.getAssetType());
        assertEquals(purchasePrice, asset.getPurchasePrice());
        assertEquals(currentValue, asset.getCurrentValue());
        assertEquals(purchaseDate, asset.getPurchaseDate());
        assertEquals(depreciationRate, asset.getDepreciationRate());
        assertEquals(createdAt, asset.getCreatedAt());
        assertEquals(updatedAt, asset.getUpdatedAt());
        assertEquals(lastSyncedAt, asset.getLastSyncedAt());
    }

    @Test
    void testEqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String assetType = "Stocks";
        long purchasePrice = 10000L;
        long currentValue = 12000L;
        LocalDate purchaseDate = LocalDate.now();
        double depreciationRate = 0.1;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastSyncedAt = LocalDateTime.now();

        AccountOtherAsset asset1 = new AccountOtherAsset(id, accountId, userId, assetType, purchasePrice, currentValue, purchaseDate, depreciationRate, createdAt, updatedAt, lastSyncedAt);
        AccountOtherAsset asset2 = new AccountOtherAsset(id, accountId, userId, assetType, purchasePrice, currentValue, purchaseDate, depreciationRate, createdAt, updatedAt, lastSyncedAt);

        assertEquals(asset1, asset2);
        assertEquals(asset1.hashCode(), asset2.hashCode());

        asset2.setPurchasePrice(20000L);
        assertNotEquals(asset1, asset2);
        assertNotEquals(asset1.hashCode(), asset2.hashCode());
    }

    @Test
    void testToString() {
        UUID id = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String assetType = "Stocks";
        long purchasePrice = 10000L;
        long currentValue = 12000L;
        LocalDate purchaseDate = LocalDate.now();
        double depreciationRate = 0.1;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastSyncedAt = LocalDateTime.now();

        AccountOtherAsset asset = new AccountOtherAsset(id, accountId, userId, assetType, purchasePrice, currentValue, purchaseDate, depreciationRate, createdAt, updatedAt, lastSyncedAt);
        String expectedString = "AccountOtherAsset(id=" + id.toString() + ", accountId=" + accountId.toString() +
                ", userId=" + userId.toString() + ", assetType=" + assetType +
                ", purchasePrice=" + purchasePrice + ", currentValue=" + currentValue +
                ", purchaseDate=" + purchaseDate + ", depreciationRate=" + depreciationRate +
                ", createdAt=" + createdAt + ", updatedAt=" + updatedAt +
                ", lastSyncedAt=" + lastSyncedAt + ")";

        assertEquals(expectedString, asset.toString());
    }
}
