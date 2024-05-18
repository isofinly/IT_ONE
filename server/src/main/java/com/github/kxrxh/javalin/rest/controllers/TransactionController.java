package com.github.kxrxh.javalin.rest.controllers;
import org.json.JSONObject;


import com.github.kxrxh.javalin.rest.api.jwt.Utils;
import com.github.kxrxh.javalin.rest.database.models.Transaction;
import com.github.kxrxh.javalin.rest.services.TransactionService;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
public class TransactionController {

    private TransactionController() {
    }

    public static void searchTransactions(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);

            String amountRange = ctx.queryParam("amount_range");
            String dateRange = ctx.queryParam("date_range");
            String categoryIdStr = ctx.queryParam("category_id");
            String description = ctx.queryParam("description");

            UUID categoryId = categoryIdStr != null ? UUID.fromString(categoryIdStr) : null;

            List<Transaction> transactions = TransactionService.searchTransactions(userId, amountRange, dateRange, categoryId, description);
            ctx.json(transactions);
        } catch (Exception e) {
            log.error(e.getMessage());
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void createTransaction(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            JSONObject requestBody = new JSONObject(ctx.body());

            String amountStr = requestBody.optString("amount");
            String categoryIdStr = requestBody.optString("category_id");
            String accountIdStr = requestBody.optString("account_id");
            String name = requestBody.optString("name");
            String dateStr = requestBody.optString("date");
            String currency = requestBody.optString("currency");
            String notes = requestBody.optString("notes");
            String transactionTypeStr = requestBody.optString("transaction_type");

            UUID categoryId = UUID.fromString(categoryIdStr);
            UUID accountId = UUID.fromString(accountIdStr);
            long amount = Long.parseLong(amountStr);
            LocalDateTime date = LocalDateTime.parse(dateStr);
            Transaction.TransactionType transactionType = Transaction.TransactionType.valueOf(transactionTypeStr.toUpperCase());

            TransactionService.createTransaction(userId, accountId, categoryId, amount, name, date, currency, notes, transactionType);
            ctx.status(200).result("Transaction created successfully");
        } catch (Exception e) {
            log.error(e.getMessage());
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void updateTransaction(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            JSONObject requestBody = new JSONObject(ctx.body());

            String transactionIdStr = requestBody.optString("transaction_id");
            UUID transactionId = UUID.fromString(transactionIdStr);

            String amountStr = requestBody.optString("amount");
            String categoryIdStr = requestBody.optString("category_id");
            String accountIdStr = requestBody.optString("account_id");
            String name = requestBody.optString("name");
            String dateStr = requestBody.optString("date");
            String currency = requestBody.optString("currency");
            String notes = requestBody.optString("notes");
            String transactionTypeStr = requestBody.optString("transaction_type");

            UUID categoryId = UUID.fromString(categoryIdStr);
            UUID accountId = UUID.fromString(accountIdStr);
            long amount = Long.parseLong(amountStr);
            LocalDateTime date = LocalDateTime.parse(dateStr);
            Transaction.TransactionType transactionType = Transaction.TransactionType.valueOf(transactionTypeStr.toUpperCase());

            TransactionService.updateTransaction(userId, transactionId, accountId, categoryId, amount, name, date, currency, notes, transactionType);
            ctx.status(200).result("Transaction updated successfully");
        } catch (Exception e) {
            log.error(e.getMessage());
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void deleteTransaction(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            JSONObject requestBody = new JSONObject(ctx.body());

            String transactionIdStr = requestBody.optString("transaction_id");
            UUID transactionId = UUID.fromString(transactionIdStr);

            TransactionService.deleteTransaction(userId, transactionId);
            ctx.status(200).result("Transaction deleted successfully");
        } catch (Exception e) {
            log.error(e.getMessage());
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void createRecurringTransaction(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            JSONObject requestBody = new JSONObject(ctx.body());

            String amountStr = requestBody.optString("amount");
            String categoryIdStr = requestBody.optString("category_id");
            String description = requestBody.optString("description");
            long frequency = Long.parseLong(Objects.requireNonNull(requestBody.optString("frequency")));

            UUID categoryId = UUID.fromString(categoryIdStr);
            long amount = Long.parseLong(amountStr);

            TransactionService.createRecurringTransaction(userId, amount, categoryId, description, frequency);
            ctx.status(200).result("Recurring transaction created successfully");
        } catch (Exception e) {
            log.error(e.getMessage());
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }
}

