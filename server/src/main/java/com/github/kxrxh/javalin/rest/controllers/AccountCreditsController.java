package com.github.kxrxh.javalin.rest.controllers;

import com.github.kxrxh.javalin.rest.api.jwt.Utils;
import com.github.kxrxh.javalin.rest.database.models.AccountCredit;
import com.github.kxrxh.javalin.rest.services.AccountCreditsService;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.UUID;

import org.json.JSONObject;

@Slf4j
public class AccountCreditsController {

    private AccountCreditsController() {
    }

    public static void createCredit(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            // Parse JSON data from request body
            JSONObject requestBody = new JSONObject(ctx.body());
   
            // Extract data from JSON
            String accountIdStr = requestBody.optString("account_id");
            String creditLimitStr = requestBody.optString("credit_limit");
            String interestRateStr = requestBody.optString("interest_rate");
            String dueDateStr = requestBody.optString("due_date");
            String minimumPaymentStr = requestBody.optString("minimum_payment");
    
            if (accountIdStr.isEmpty() || creditLimitStr.isEmpty() || interestRateStr.isEmpty() || dueDateStr.isEmpty() || minimumPaymentStr.isEmpty()) {
                ctx.status(400).result("Missing required parameters");
                return;
            }
    
            UUID accountId = UUID.fromString(accountIdStr);
            long creditLimit = Long.parseLong(creditLimitStr);
            double interestRate = Double.parseDouble(interestRateStr);
            LocalDate dueDate = LocalDate.parse(dueDateStr);
            long minimumPayment = Long.parseLong(minimumPaymentStr);
    
            AccountCreditsService.createCredit(userId, accountId, creditLimit, interestRate, dueDate, minimumPayment);
            ctx.status(200).result("Credit created successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void readCredit(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            JSONObject requestBody = new JSONObject(ctx.body());

            String creditIdStr = requestBody.optString("credit_id");

            if (creditIdStr.isEmpty()) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID creditId = UUID.fromString(creditIdStr);
            AccountCredit credit = AccountCreditsService.readCredit(userId, creditId);
            ctx.json(credit);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void updateCredit(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            JSONObject requestBody = new JSONObject(ctx.body());

            String creditIdStr = requestBody.optString("credit_id");
            String accountIdStr = requestBody.optString("account_id");
            String creditLimitStr = requestBody.optString("credit_limit");
            String interestRateStr = requestBody.optString("interest_rate");
            String dueDateStr = requestBody.optString("due_date");
            String minimumPaymentStr = requestBody.optString("minimum_payment");

            if (accountIdStr.isEmpty() || creditLimitStr.isEmpty() || interestRateStr.isEmpty() || dueDateStr.isEmpty() || minimumPaymentStr.isEmpty() || creditIdStr.isEmpty()) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID creditId = UUID.fromString(creditIdStr);
            UUID accountId = UUID.fromString(accountIdStr);
            long creditLimit = Long.parseLong(creditLimitStr);
            double interestRate = Double.parseDouble(interestRateStr);
            LocalDate dueDate = LocalDate.parse(dueDateStr);
            long minimumPayment = Long.parseLong(minimumPaymentStr);

            AccountCreditsService.updateCredit(userId, creditId, accountId, creditLimit, interestRate, dueDate, minimumPayment);
            ctx.status(200).result("Credit updated successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void deleteCredit(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            JSONObject requestBody = new JSONObject(ctx.body());

            String creditIdStr = requestBody.optString("credit_id");

            if (creditIdStr.isEmpty()) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID creditId = UUID.fromString(creditIdStr);
            AccountCreditsService.deleteCredit(userId, creditId);
            ctx.status(200).result("Credit deleted successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void calculateInterest(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);

            String creditIdStr = ctx.queryParam("credit_id");

            if (creditIdStr.isEmpty()) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID creditId = UUID.fromString(creditIdStr);
            AccountCreditsService.calculateInterest(userId, creditId);
            ctx.status(200).result("Interest calculated and updated successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }
}
