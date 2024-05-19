package com.github.kxrxh.javalin.rest.controllers;

import com.github.kxrxh.javalin.rest.api.jwt.Utils;
import com.github.kxrxh.javalin.rest.database.models.AccountCredit;
import com.github.kxrxh.javalin.rest.services.AccountCreditsService;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Slf4j
public class AccountCreditsController extends AbstractController {

    public static void createCredit(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);
        // Parse JSON data from request body
        JSONObject requestBody;
        try {
            requestBody = new JSONObject(ctx.body());
        } catch (JSONException e) {
            ctx.status(400).result(WRONG_BODY_FORMAT + e.getMessage());
            return;
        }
        // Extract data from JSON
        String accountName = requestBody.optString("account_name");
        String creditLimitStr = requestBody.optString("credit_limit");
        String interestRateStr = requestBody.optString("interest_rate");
        String dueDateStr = requestBody.optString("due_date");
        String minimumPaymentStr = requestBody.optString("minimum_payment");

        if (accountName.isEmpty() || creditLimitStr.isEmpty() || interestRateStr.isEmpty() || dueDateStr.isEmpty()
                || minimumPaymentStr.isEmpty()) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }
        long creditLimit = Long.parseLong(creditLimitStr);
        double interestRate = Double.parseDouble(interestRateStr);
        LocalDate dueDate = LocalDate.parse(dueDateStr);
        long minimumPayment = Long.parseLong(minimumPaymentStr);
        try {
            UUID accountId = AccountCreditsService.createAccount(userId, "Credit", accountName);
            AccountCreditsService.createCredit(accountId, creditLimit, interestRate, dueDate, minimumPayment);
            ctx.status(200).result("Credit created successfully");
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }


    public static void readCredit(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);

        try {
            AccountCredit[] credits = AccountCreditsService.readCredit(userId);
            ctx.json(credits);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }

    }

    public static void updateCredit(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);
        JSONObject requestBody;
        try {
            requestBody = new JSONObject(ctx.body());
        } catch (JSONException e) {
            ctx.status(400).result(WRONG_BODY_FORMAT + e.getMessage());
            return;
        }
        String creditIdStr = requestBody.optString(CREDIT_ID);
        String accountIdStr = requestBody.optString("account_id");
        String creditLimitStr = requestBody.optString("credit_limit");
        String interestRateStr = requestBody.optString("interest_rate");
        String dueDateStr = requestBody.optString("due_date");
        String minimumPaymentStr = requestBody.optString("minimum_payment");

        if (accountIdStr.isEmpty() || creditLimitStr.isEmpty() || interestRateStr.isEmpty() || dueDateStr.isEmpty()
                || minimumPaymentStr.isEmpty() || creditIdStr.isEmpty()) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        UUID creditId = UUID.fromString(creditIdStr);
        UUID accountId = UUID.fromString(accountIdStr);
        long creditLimit = Long.parseLong(creditLimitStr);
        double interestRate = Double.parseDouble(interestRateStr);
        LocalDate dueDate = LocalDate.parse(dueDateStr);
        long minimumPayment = Long.parseLong(minimumPaymentStr);

        try {
            AccountCreditsService.updateCredit(userId, creditId, accountId, creditLimit, interestRate, dueDate, minimumPayment);
            ctx.status(200).result("Credit updated successfully");
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    public static void deleteCredit(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);
        JSONObject requestBody;
        try {
            requestBody = new JSONObject(ctx.body());
        } catch (JSONException e) {
            ctx.status(400).result(WRONG_BODY_FORMAT + e.getMessage());
            return;
        }
        String creditIdStr = requestBody.optString(CREDIT_ID);

        if (creditIdStr.isEmpty()) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        UUID creditId = UUID.fromString(creditIdStr);
        try {
            AccountCreditsService.deleteCredit(userId, creditId);
            ctx.status(200).result("Credit deleted successfully");
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    public static void calculateInterest(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);

        String creditIdStr = ctx.queryParam(CREDIT_ID);

        if (Objects.requireNonNull(creditIdStr).isEmpty()) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        UUID creditId = UUID.fromString(creditIdStr);
        try {
            AccountCreditsService.calculateInterest(userId, creditId);
            ctx.status(200).result("Interest calculated and updated successfully");
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }
}
