package com.github.kxrxh.javalin.rest.controllers;

import com.github.kxrxh.javalin.rest.api.jwt.Utils;
import com.github.kxrxh.javalin.rest.database.models.Transaction;
import com.github.kxrxh.javalin.rest.services.TransactionService;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
public class TransactionController extends AbstractController {

    private TransactionController() {
    }

    public static void searchTransactions(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);

        String amountRange = ctx.queryParam("amount_range");
        String dateRange = ctx.queryParam("date_range");
        String categoryIdStr = ctx.queryParam(CATEGORY_ID);
        String description = ctx.queryParam("description");

        if (dateRange != null) {
            dateRange = dateRange.replace("_", " ");
        }

        UUID categoryId = categoryIdStr != null ? UUID.fromString(categoryIdStr) : null;

        try {
            List<Transaction> transactions = TransactionService.searchTransactions(userId, amountRange, dateRange,
                    categoryId, description);
            ctx.json(transactions);
        } catch (Exception e) {
            log.error(e.getMessage());
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    public static void createTransaction(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);
        JSONObject requestBody;
        try {
            requestBody = new JSONObject(ctx.body());
        } catch (JSONException e) {
            ctx.status(400).result(WRONG_BODY_FORMAT + e.getMessage());
            return;
        }
        String amountStr = requestBody.optString(AMOUNT);
        String categoryIdStr = requestBody.optString(CATEGORY_ID);
        String accountIdStr = requestBody.optString(ACCOUNT_ID);
        String name = requestBody.optString(NAME);
        String dateStr = requestBody.optString(DATE);
        String currency = requestBody.optString(CURRENCY);
        String notes = requestBody.optString("notes");
        String transactionTypeStr = requestBody.optString("transaction_type");

        UUID categoryId = UUID.fromString(categoryIdStr);
        UUID accountId = UUID.fromString(accountIdStr);
        long amount = Long.parseLong(amountStr);
        LocalDateTime date = Timestamp.valueOf(dateStr).toLocalDateTime();

        Transaction.TransactionType transactionType = Transaction.TransactionType
                .valueOf(transactionTypeStr.toUpperCase());

        try {
            TransactionService.createTransaction(userId, accountId, categoryId, amount, name, date, currency, notes,
                    transactionType);
            ctx.status(200).result("Transaction created successfully");
        } catch (Exception e) {
            log.error(e.getMessage());
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    public static void updateTransaction(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);
        JSONObject requestBody;
        try {
            requestBody = new JSONObject(ctx.body());
        } catch (JSONException e) {
            ctx.status(400).result(WRONG_BODY_FORMAT + e.getMessage());
            return;
        }
        String transactionIdStr = requestBody.optString("transaction_id");
        UUID transactionId = UUID.fromString(transactionIdStr);

        String amountStr = requestBody.optString(AMOUNT);
        String categoryIdStr = requestBody.optString(CATEGORY_ID);
        String accountIdStr = requestBody.optString(ACCOUNT_ID);
        String name = requestBody.optString(NAME);
        String dateStr = requestBody.optString(DATE);
        String currency = requestBody.optString(CURRENCY);
        String notes = requestBody.optString("notes");
        String transactionTypeStr = requestBody.optString("transaction_type");

        UUID categoryId = UUID.fromString(categoryIdStr);
        UUID accountId = UUID.fromString(accountIdStr);
        long amount = Long.parseLong(amountStr);
        LocalDateTime date = LocalDateTime.parse(dateStr);
        Transaction.TransactionType transactionType = Transaction.TransactionType
                .valueOf(transactionTypeStr.toUpperCase());

        try {
            TransactionService.updateTransaction(userId, transactionId, accountId, categoryId, amount, name, date,
                    currency, notes, transactionType);
            ctx.status(200).result("Transaction updated successfully");
        } catch (Exception e) {
            log.error(e.getMessage());
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    public static void deleteTransaction(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);
        JSONObject requestBody;
        try {
            requestBody = new JSONObject(ctx.body());
        } catch (JSONException e) {
            ctx.status(400).result(WRONG_BODY_FORMAT + e.getMessage());
            return;
        }
        String transactionIdStr = requestBody.optString("transaction_id");
        UUID transactionId = UUID.fromString(transactionIdStr);

        try {
            TransactionService.deleteTransaction(userId, transactionId);
            ctx.status(200).result("Transaction deleted successfully");
        } catch (Exception e) {
            log.error(e.getMessage());
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    public static void createRecurringTransaction(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);
        JSONObject requestBody;
        try {
            requestBody = new JSONObject(ctx.body());
        } catch (JSONException e) {
            ctx.status(400).result(WRONG_BODY_FORMAT + e.getMessage());
            return;
        }
        String amountStr = requestBody.optString(AMOUNT);
        String categoryIdStr = requestBody.optString(CATEGORY_ID);
        String description = requestBody.optString("description");
        long frequency = Long.parseLong(Objects.requireNonNull(requestBody.optString("frequency")));

        UUID categoryId = UUID.fromString(categoryIdStr);
        long amount = Long.parseLong(amountStr);

        try {
            TransactionService.createRecurringTransaction(userId, amount, categoryId, description, frequency);
            ctx.status(200).result("Recurring transaction created successfully");
        } catch (Exception e) {
            log.error(e.getMessage());
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }
}
