package com.github.kxrxh.javalin.rest.controllers;

import com.github.kxrxh.javalin.rest.api.jwt.Utils;
import com.github.kxrxh.javalin.rest.database.models.RecurringTransaction;
import com.github.kxrxh.javalin.rest.services.RecurringTransactionService;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.UUID;

@Slf4j
public class RecurringTransactionController extends AbstractController {

    private RecurringTransactionController() {
    }

    public static void createRecurringTransaction(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);
        JSONObject requestBody = new JSONObject(ctx.body());

        String amountStr = requestBody.optString("amount");
        String categoryIdStr = requestBody.optString("category_id");
        String description = requestBody.optString("description");
        String frequencyStr = requestBody.optString("frequency");

        if (amountStr == null || frequencyStr == null) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        long amount = Long.parseLong(amountStr);
        long frequency = Long.parseLong(frequencyStr);
        UUID categoryId = categoryIdStr != null ? UUID.fromString(categoryIdStr) : null;

        try {
            RecurringTransactionService.createRecurringTransaction(userId, amount, categoryId, description, frequency);
            ctx.status(200).result("Recurring transaction created successfully");
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    public static void readRecurringTransaction(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);

        String recurringTransactionIdStr = ctx.queryParam(RECURRING_TRANSACTION_ID);

        if (recurringTransactionIdStr == null || recurringTransactionIdStr.isEmpty()) {
            ctx.status(400).result("Missing required parameters");
            return;
        }

        UUID recurringTransactionId = UUID.fromString(recurringTransactionIdStr);

        try {
            RecurringTransaction recurringTransaction = RecurringTransactionService
                    .readRecurringTransaction(userId, recurringTransactionId);
            ctx.json(recurringTransaction);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    public static void updateRecurringTransaction(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);
        JSONObject requestBody = new JSONObject(ctx.body());

        String recurringTransactionIdStr = requestBody.optString(RECURRING_TRANSACTION_ID);
        String amountStr = requestBody.optString("amount");
        String categoryIdStr = requestBody.optString("category_id");
        String description = requestBody.optString("description");
        String frequencyStr = requestBody.optString("frequency");

        if (recurringTransactionIdStr == null || amountStr == null || frequencyStr == null) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        UUID recurringTransactionId = UUID.fromString(recurringTransactionIdStr);
        long amount = Long.parseLong(amountStr);
        long frequency = Long.parseLong(frequencyStr);
        UUID categoryId = categoryIdStr != null ? UUID.fromString(categoryIdStr) : null;

        try {
            RecurringTransactionService.updateRecurringTransaction(userId, recurringTransactionId, amount, categoryId,
                    description, frequency);
            ctx.status(200).result("Recurring transaction updated successfully");
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    public static void deleteRecurringTransaction(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);
        JSONObject requestBody = new JSONObject(ctx.body());

        String recurringTransactionIdStr = requestBody.optString(RECURRING_TRANSACTION_ID);

        if (recurringTransactionIdStr == null) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        UUID recurringTransactionId = UUID.fromString(recurringTransactionIdStr);

        try {
            RecurringTransactionService.deleteRecurringTransaction(userId, recurringTransactionId);
            ctx.status(200).result("Recurring transaction deleted successfully");
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }
}