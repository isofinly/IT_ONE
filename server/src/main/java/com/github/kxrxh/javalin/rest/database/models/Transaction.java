package com.github.kxrxh.javalin.rest.database.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    private UUID transactionId;
    private String name;
    private LocalDateTime date;
    private long amount;
    private String currency;
    private UUID accountId;
    private UUID categoryId;
    private boolean excluded;
    private String notes;
    private TransactionType transactionType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastSyncedAt;
    private List<Transaction> transactions;

    public enum TransactionType {
        INFLOW, OUTFLOW
    }
    
        public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
}
