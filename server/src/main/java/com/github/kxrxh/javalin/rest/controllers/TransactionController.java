package com.github.kxrxh.javalin.rest.controllers;

import com.github.kxrxh.javalin.rest.api.jwt.Utils;
import com.github.kxrxh.javalin.rest.database.models.Transaction;
import com.github.kxrxh.javalin.rest.services.TransactionService;

import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
public class TransactionController {
    // TODO: check user_id and other fields from token

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

            List<Transaction> transactions = TransactionService.searchTransactions(userId, amountRange, dateRange,
                    categoryId, description);
            ctx.json(transactions);
        } catch (Exception e) {
            log.error(e.getMessage());
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void createRecurringTransaction(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            String amountStr = ctx.formParam("amount");
            String categoryIdStr = ctx.formParam("category_id");
            String description = ctx.formParam("description");
            Long frequency = Long.valueOf(Objects.requireNonNull(ctx.formParam("frequency")));

            UUID categoryId = UUID.fromString(categoryIdStr);
            Long amount = Long.parseLong(amountStr);

            TransactionService.createRecurringTransaction(userId, amount, categoryId, description, frequency);
            ctx.status(200).result("Recurring transaction created successfully");
        } catch (Exception e) {
            log.error(e.getMessage());
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }
}
