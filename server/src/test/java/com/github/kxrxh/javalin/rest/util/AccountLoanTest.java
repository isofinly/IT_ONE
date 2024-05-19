package com.github.kxrxh.javalin.rest.util;

import com.github.kxrxh.javalin.rest.database.models.AccountLoan;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AccountLoanTest {

    @Test
    void testNoArgsConstructor() {
        AccountLoan loan = new AccountLoan();
        assertNotNull(loan, "AccountLoan object should not be null");
    }

    @Test
    void testAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        long loanAmount = 10000L;
        long outstandingBalance = 8000L;
        double interestRate = 0.05;
        String loanTerm = "5 years";
        LocalDate dueDate = LocalDate.now().plusYears(5);
        String paymentFrequency = "monthly";
        String collateral = "House";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastSyncedAt = LocalDateTime.now();

        AccountLoan loan = new AccountLoan(id, accountId, userId, loanAmount, outstandingBalance, interestRate, loanTerm, dueDate, paymentFrequency, collateral, createdAt, updatedAt, lastSyncedAt);
        assertEquals(id, loan.getId());
        assertEquals(accountId, loan.getAccountId());
        assertEquals(userId, loan.getUserId());
        assertEquals(loanAmount, loan.getLoanAmount());
        assertEquals(outstandingBalance, loan.getOutstandingBalance());
        assertEquals(interestRate, loan.getInterestRate());
        assertEquals(loanTerm, loan.getLoanTerm());
        assertEquals(dueDate, loan.getDueDate());
        assertEquals(paymentFrequency, loan.getPaymentFrequency());
        assertEquals(collateral, loan.getCollateral());
        assertEquals(createdAt, loan.getCreatedAt());
        assertEquals(updatedAt, loan.getUpdatedAt());
        assertEquals(lastSyncedAt, loan.getLastSyncedAt());
    }

    @Test
    void testBuilder() {
        UUID id = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        long loanAmount = 20000L;
        long outstandingBalance = 15000L;
        double interestRate = 0.06;
        String loanTerm = "10 years";
        LocalDate dueDate = LocalDate.now().plusYears(10);
        String paymentFrequency = "biweekly";
        String collateral = "Car";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastSyncedAt = LocalDateTime.now();

        AccountLoan loan = AccountLoan.builder()
                .id(id)
                .accountId(accountId)
                .userId(userId)
                .loanAmount(loanAmount)
                .outstandingBalance(outstandingBalance)
                .interestRate(interestRate)
                .loanTerm(loanTerm)
                .dueDate(dueDate)
                .paymentFrequency(paymentFrequency)
                .collateral(collateral)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .lastSyncedAt(lastSyncedAt)
                .build();

        assertEquals(id, loan.getId());
        assertEquals(accountId, loan.getAccountId());
        assertEquals(userId, loan.getUserId());
        assertEquals(loanAmount, loan.getLoanAmount());
        assertEquals(outstandingBalance, loan.getOutstandingBalance());
        assertEquals(interestRate, loan.getInterestRate());
        assertEquals(loanTerm, loan.getLoanTerm());
        assertEquals(dueDate, loan.getDueDate());
        assertEquals(paymentFrequency, loan.getPaymentFrequency());
        assertEquals(collateral, loan.getCollateral());
        assertEquals(createdAt, loan.getCreatedAt());
        assertEquals(updatedAt, loan.getUpdatedAt());
        assertEquals(lastSyncedAt, loan.getLastSyncedAt());
    }

    @Test
    void testSettersAndGetters() {
        AccountLoan loan = new AccountLoan();

        UUID id = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        long loanAmount = 30000L;
        long outstandingBalance = 25000L;
        double interestRate = 0.07;
        String loanTerm = "15 years";
        LocalDate dueDate = LocalDate.now().plusYears(15);
        String paymentFrequency = "weekly";
        String collateral = "Jewelry";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastSyncedAt = LocalDateTime.now();

        loan.setId(id);
        loan.setAccountId(accountId);
        loan.setUserId(userId);
        loan.setLoanAmount(loanAmount);
        loan.setOutstandingBalance(outstandingBalance);
        loan.setInterestRate(interestRate);
        loan.setLoanTerm(loanTerm);
        loan.setDueDate(dueDate);
        loan.setPaymentFrequency(paymentFrequency);
        loan.setCollateral(collateral);
        loan.setCreatedAt(createdAt);
        loan.setUpdatedAt(updatedAt);
        loan.setLastSyncedAt(lastSyncedAt);

        assertEquals(id, loan.getId());
        assertEquals(accountId, loan.getAccountId());
        assertEquals(userId, loan.getUserId());
        assertEquals(loanAmount, loan.getLoanAmount());
        assertEquals(outstandingBalance, loan.getOutstandingBalance());
        assertEquals(interestRate, loan.getInterestRate());
        assertEquals(loanTerm, loan.getLoanTerm());
        assertEquals(dueDate, loan.getDueDate());
        assertEquals(paymentFrequency, loan.getPaymentFrequency());
        assertEquals(collateral, loan.getCollateral());
        assertEquals(createdAt, loan.getCreatedAt());
        assertEquals(updatedAt, loan.getUpdatedAt());
        assertEquals(lastSyncedAt, loan.getLastSyncedAt());
    }

    @Test
    void testEqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        long loanAmount = 10000L;
        long outstandingBalance = 8000L;
        double interestRate = 0.05;
        String loanTerm = "5 years";
        LocalDate dueDate = LocalDate.now().plusYears(5);
        String paymentFrequency = "monthly";
        String collateral = "House";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastSyncedAt = LocalDateTime.now();

        AccountLoan loan1 = new AccountLoan(id, accountId, userId, loanAmount, outstandingBalance, interestRate, loanTerm, dueDate, paymentFrequency, collateral, createdAt, updatedAt, lastSyncedAt);
        AccountLoan loan2 = new AccountLoan(id, accountId, userId, loanAmount, outstandingBalance, interestRate, loanTerm, dueDate, paymentFrequency, collateral, createdAt, updatedAt, lastSyncedAt);

        assertEquals(loan1, loan2);
        assertEquals(loan1.hashCode(), loan2.hashCode());

        loan2.setLoanAmount(20000L);
        assertNotEquals(loan1, loan2);
        assertNotEquals(loan1.hashCode(), loan2.hashCode());
    }

    @Test
    void testToString() {
        UUID id = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        long loanAmount = 10000L;
        long outstandingBalance = 8000L;
        double interestRate = 0.05;
        String loanTerm = "5 years";
        LocalDate dueDate = LocalDate.now().plusYears(5);
        String paymentFrequency = "monthly";
        String collateral = "House";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastSyncedAt = LocalDateTime.now();

        AccountLoan loan = new AccountLoan(id, accountId, userId, loanAmount, outstandingBalance, interestRate, loanTerm, dueDate, paymentFrequency, collateral, createdAt, updatedAt, lastSyncedAt);
        String expectedString = "AccountLoan(id=" + id.toString() + ", accountId=" + accountId.toString() +
                ", userId=" + userId.toString() + ", loanAmount=" + loanAmount + ", outstandingBalance=" +
                outstandingBalance + ", interestRate=" + interestRate + ", loanTerm=" + loanTerm +
                ", dueDate=" + dueDate + ", paymentFrequency=" + paymentFrequency + ", collateral=" +
                collateral + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt +
                ", lastSyncedAt=" + lastSyncedAt + ")";

        assertEquals(expectedString, loan.toString());
    }
}
