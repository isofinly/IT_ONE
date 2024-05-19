package com.github.kxrxh.javalin.rest.util;

import com.github.kxrxh.javalin.rest.database.models.AccountInvestment;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AccountInvestmentTest {

    @Test
    void testNoArgsConstructor() {
        AccountInvestment investment = new AccountInvestment();
        assertNotNull(investment, "AccountInvestment object should not be null");
    }

    @Test
    void testAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        String investmentType = "Stocks";
        long marketValue = 5000L;
        long purchasePrice = 4000L;
        LocalDate purchaseDate = LocalDate.now();
        long dividends = 200L;
        long interestRate = 5L;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastSyncedAt = LocalDateTime.now();

        AccountInvestment investment = new AccountInvestment(id, accountId, investmentType, marketValue, purchasePrice, purchaseDate, dividends, interestRate, createdAt, updatedAt, lastSyncedAt);
        assertEquals(id, investment.getId());
        assertEquals(accountId, investment.getAccountId());
        assertEquals(investmentType, investment.getInvestmentType());
        assertEquals(marketValue, investment.getMarketValue());
        assertEquals(purchasePrice, investment.getPurchasePrice());
        assertEquals(purchaseDate, investment.getPurchaseDate());
        assertEquals(dividends, investment.getDividends());
        assertEquals(interestRate, investment.getInterestRate());
        assertEquals(createdAt, investment.getCreatedAt());
        assertEquals(updatedAt, investment.getUpdatedAt());
        assertEquals(lastSyncedAt, investment.getLastSyncedAt());
    }

    @Test
    void testBuilder() {
        UUID id = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        String investmentType = "Bonds";
        long marketValue = 7000L;
        long purchasePrice = 6000L;
        LocalDate purchaseDate = LocalDate.now();
        long dividends = 300L;
        long interestRate = 7L;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastSyncedAt = LocalDateTime.now();

        AccountInvestment investment = AccountInvestment.builder()
                .id(id)
                .accountId(accountId)
                .investmentType(investmentType)
                .marketValue(marketValue)
                .purchasePrice(purchasePrice)
                .purchaseDate(purchaseDate)
                .dividends(dividends)
                .interestRate(interestRate)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .lastSyncedAt(lastSyncedAt)
                .build();

        assertEquals(id, investment.getId());
        assertEquals(accountId, investment.getAccountId());
        assertEquals(investmentType, investment.getInvestmentType());
        assertEquals(marketValue, investment.getMarketValue());
        assertEquals(purchasePrice, investment.getPurchasePrice());
        assertEquals(purchaseDate, investment.getPurchaseDate());
        assertEquals(dividends, investment.getDividends());
        assertEquals(interestRate, investment.getInterestRate());
        assertEquals(createdAt, investment.getCreatedAt());
        assertEquals(updatedAt, investment.getUpdatedAt());
        assertEquals(lastSyncedAt, investment.getLastSyncedAt());
    }

    @Test
    void testSettersAndGetters() {
        AccountInvestment investment = new AccountInvestment();

        UUID id = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        String investmentType = "Mutual Funds";
        long marketValue = 9000L;
        long purchasePrice = 8000L;
        LocalDate purchaseDate = LocalDate.now();
        long dividends = 400L;
        long interestRate = 6L;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastSyncedAt = LocalDateTime.now();

        investment.setId(id);
        investment.setAccountId(accountId);
        investment.setInvestmentType(investmentType);
        investment.setMarketValue(marketValue);
        investment.setPurchasePrice(purchasePrice);
        investment.setPurchaseDate(purchaseDate);
        investment.setDividends(dividends);
        investment.setInterestRate(interestRate);
        investment.setCreatedAt(createdAt);
        investment.setUpdatedAt(updatedAt);
        investment.setLastSyncedAt(lastSyncedAt);

        assertEquals(id, investment.getId());
        assertEquals(accountId, investment.getAccountId());
        assertEquals(investmentType, investment.getInvestmentType());
        assertEquals(marketValue, investment.getMarketValue());
        assertEquals(purchasePrice, investment.getPurchasePrice());
        assertEquals(purchaseDate, investment.getPurchaseDate());
        assertEquals(dividends, investment.getDividends());
        assertEquals(interestRate, investment.getInterestRate());
        assertEquals(createdAt, investment.getCreatedAt());
        assertEquals(updatedAt, investment.getUpdatedAt());
        assertEquals(lastSyncedAt, investment.getLastSyncedAt());
    }

    @Test
    void testEqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        String investmentType = "Stocks";
        long marketValue = 5000L;
        long purchasePrice = 4000L;
        LocalDate purchaseDate = LocalDate.now();
        long dividends = 200L;
        long interestRate = 5L;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastSyncedAt = LocalDateTime.now();

        AccountInvestment investment1 = new AccountInvestment(id, accountId, investmentType, marketValue, purchasePrice, purchaseDate, dividends, interestRate, createdAt, updatedAt, lastSyncedAt);
        AccountInvestment investment2 = new AccountInvestment(id, accountId, investmentType, marketValue, purchasePrice, purchaseDate, dividends, interestRate, createdAt, updatedAt, lastSyncedAt);

        assertEquals(investment1, investment2);
        assertEquals(investment1.hashCode(), investment2.hashCode());

        investment2.setMarketValue(6000L);
        assertNotEquals(investment1, investment2);
        assertNotEquals(investment1.hashCode(), investment2.hashCode());
    }

    @Test
    void testToString() {
        UUID id = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        String investmentType = "Stocks";
        long marketValue = 5000L;
        long purchasePrice = 4000L;
        LocalDate purchaseDate = LocalDate.now();
        long dividends = 200L;
        long interestRate = 5L;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastSyncedAt = LocalDateTime.now();

        AccountInvestment investment = new AccountInvestment(id, accountId, investmentType, marketValue, purchasePrice, purchaseDate, dividends, interestRate, createdAt, updatedAt, lastSyncedAt);
        String expectedString = "AccountInvestment(id=" + id.toString() + ", accountId=" + accountId.toString() +
                ", investmentType=" + investmentType + ", marketValue=" + marketValue + ", purchasePrice=" + purchasePrice +
                ", purchaseDate=" + purchaseDate + ", dividends=" + dividends + ", interestRate=" + interestRate +
                ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", lastSyncedAt=" + lastSyncedAt + ")";

        assertEquals(expectedString, investment.toString());
    }
}
