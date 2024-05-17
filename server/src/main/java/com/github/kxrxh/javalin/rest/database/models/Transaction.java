package com.github.kxrxh.javalin.rest.database.models;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class Transaction {

    private Long transactionId;
    private Long amount;
    private Timestamp transactionDate;
    private Long categoryId;
    private Long userId;
    private String description;

    public Transaction(Long transactionId, Long amount, Timestamp transactionDate, Long categoryId, Long userId, String description) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.categoryId = categoryId;
        this.userId = userId;
        this.description = description;
    }

    // Getters and setters omitted for brevity
}
