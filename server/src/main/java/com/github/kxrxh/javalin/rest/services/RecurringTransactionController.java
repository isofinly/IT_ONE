package com.github.kxrxh.javalin.rest.services;

import com.github.kxrxh.javalin.rest.api.jwt.Utils;
import com.github.kxrxh.javalin.rest.database.models.RecurringTransaction;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;


@Slf4j
public class RecurringTransactionController {

    private RecurringTransactionController() {
    }

    public static void createRecurringTransaction(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            String amountStr = ctx.formParam("amount");
            String categoryIdStr = ctx.formParam("category_id");
            String description = ctx.formParam("description");
            String frequencyStr = ctx.formParam("frequency");

            if (amountStr == null || frequencyStr == null) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            long amount = Long.parseLong(amountStr);
            long frequency = Long.parseLong(frequencyStr);
            UUID categoryId = categoryIdStr != null ? UUID.fromString(categoryIdStr) : null;

            RecurringTransactionService.createRecurringTransaction(userId, amount, categoryId, description, frequency);
            ctx.status(200).result("Recurring transaction created successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void readRecurringTransaction(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            String recurringTransactionIdStr = ctx.pathParam("recurring_transaction_id");

            if (recurringTransactionIdStr == null) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID recurringTransactionId = UUID.fromString(recurringTransactionIdStr);
            RecurringTransaction recurringTransaction = RecurringTransactionService.readRecurringTransaction(userId, recurringTransactionId);
            ctx.json(recurringTransaction);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void updateRecurringTransaction(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            String recurringTransactionIdStr = ctx.pathParam("recurring_transaction_id");
            String amountStr = ctx.formParam("amount");
            String categoryIdStr = ctx.formParam("category_id");
            String description = ctx.formParam("description");
            String frequencyStr = ctx.formParam("frequency");

            if (recurringTransactionIdStr == null || amountStr == null || frequencyStr == null) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID recurringTransactionId = UUID.fromString(recurringTransactionIdStr);
            long amount = Long.parseLong(amountStr);
            long frequency = Long.parseLong(frequencyStr);
            UUID categoryId = categoryIdStr != null ? UUID.fromString(categoryIdStr) : null;

            RecurringTransactionService.updateRecurringTransaction(userId, recurringTransactionId, amount, categoryId, description, frequency);
            ctx.status(200).result("Recurring transaction updated successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void deleteRecurringTransaction(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            String recurringTransactionIdStr = ctx.pathParam("recurring_transaction_id");

            if (recurringTransactionIdStr == null) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID recurringTransactionId = UUID.fromString(recurringTransactionIdStr);
            RecurringTransactionService.deleteRecurringTransaction(userId, recurringTransactionId);
            ctx.status(200).result("Recurring transaction deleted successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }
}
