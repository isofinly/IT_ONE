package com.github.kxrxh.javalin.rest.controllers;

import com.github.kxrxh.javalin.rest.database.models.Transaction;
import com.github.kxrxh.javalin.rest.services.TransactionService;
import io.javalin.http.Context;

import java.util.List;

public class TransactionController {

    private static final TransactionService transactionService = new TransactionService();

    public static void searchTransactions(Context ctx) {
        try {
            String userIdStr = ctx.queryParam("user_id");
            String amountRange = ctx.queryParam("amount_range");
            String dateRange = ctx.queryParam("date_range");
            String categoryIdStr = ctx.queryParam("category_id");
            String description = ctx.queryParam("description");

            if (userIdStr == null) {
                ctx.status(400).result("Missing required parameter: user_id");
                return;
            }

            Long userId = Long.parseLong(userIdStr);
            Long categoryId = categoryIdStr != null ? Long.parseLong(categoryIdStr) : null;

            List<Transaction> transactions = transactionService.searchTransactions(userId, amountRange, dateRange, categoryId, description);
            ctx.json(transactions);
        } catch (Exception e) {
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void createRecurringTransaction(Context ctx) {
        try {
            String userIdStr = ctx.formParam("user_id");
            String amountStr = ctx.formParam("amount");
            String categoryIdStr = ctx.formParam("category_id");
            String description = ctx.formParam("description");
            String frequency = ctx.formParam("frequency");

            if (userIdStr == null || amountStr == null || categoryIdStr == null || frequency == null) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            Long userId = Long.parseLong(userIdStr);
            Long categoryId = Long.parseLong(categoryIdStr);
            Long amount = Long.parseLong(amountStr);

            transactionService.createRecurringTransaction(userId, amount, categoryId, description, frequency);
            ctx.status(200).result("Recurring transaction created successfully");
        } catch (Exception e) {
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }
}
