package com.github.kxrxh.javalin.rest.controllers;

import com.github.kxrxh.javalin.rest.api.jwt.Utils;
import com.github.kxrxh.javalin.rest.database.models.AccountDepository;
import com.github.kxrxh.javalin.rest.services.AccountDepositoriesService;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.UUID;

@Slf4j
public class AccountDepositoriesController extends AbstractController {

    private AccountDepositoriesController() {
    }

    public static void createDepository(Context ctx) {

        // Parse JSON data from request body
        JSONObject requestBody;
        try {
            requestBody = new JSONObject(ctx.body());
        } catch (JSONException e) {
            ctx.status(400).result(WRONG_BODY_FORMAT + e.getMessage());
            return;
        }
        // Extract data from JSON
        String accountIdStr = requestBody.optString("account_id");
        String bankName = requestBody.optString("bank_name");
        String accountNumber = requestBody.optString("account_number");
        String routingNumber = requestBody.optString("routing_number");
        String interestRateStr = requestBody.optString("interest_rate");
        String overdraftLimitStr = requestBody.optString("overdraft_limit");

        if (accountIdStr.isEmpty() || bankName.isEmpty() || accountNumber.isEmpty() || routingNumber.isEmpty()
                || interestRateStr.isEmpty() || overdraftLimitStr.isEmpty()) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        UUID accountId = UUID.fromString(accountIdStr);
        double interestRate = Double.parseDouble(interestRateStr);
        long overdraftLimit = Long.parseLong(overdraftLimitStr);

        try {
            AccountDepositoriesService.createDepository(accountId, bankName, accountNumber, routingNumber,
                    interestRate, overdraftLimit);
            ctx.status(200).result("Depository created successfully");
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    public static void readDepository(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);

            String depositoryIdStr = ctx.queryParam(DEPOSITORY_ID);

            if (depositoryIdStr == null || depositoryIdStr.isEmpty()) {
                ctx.status(400).result(MISSING_REQUIERED_STRING);
                return;
            }

            UUID depositoryId = UUID.fromString(depositoryIdStr);
            AccountDepository depository = AccountDepositoriesService.readDepository(userId, depositoryId);
            ctx.json(depository);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    public static void updateDepository(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);
        JSONObject requestBody;
        try {
            requestBody = new JSONObject(ctx.body());
        } catch (JSONException e) {
            ctx.status(400).result(WRONG_BODY_FORMAT + e.getMessage());
            return;
        }
        String depositoryIdStr = requestBody.optString(DEPOSITORY_ID);
        String accountIdStr = requestBody.optString("account_id");
        String bankName = requestBody.optString("bank_name");
        String accountNumber = requestBody.optString("account_number");
        String routingNumber = requestBody.optString("routing_number");
        String interestRateStr = requestBody.optString("interest_rate");
        String overdraftLimitStr = requestBody.optString("overdraft_limit");

        if (depositoryIdStr.isEmpty() || accountIdStr.isEmpty() || bankName.isEmpty() || accountNumber.isEmpty()
                || routingNumber.isEmpty() || interestRateStr.isEmpty() || overdraftLimitStr.isEmpty()) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        UUID depositoryId = UUID.fromString(depositoryIdStr);
        UUID accountId = UUID.fromString(accountIdStr);
        double interestRate = Double.parseDouble(interestRateStr);
        long overdraftLimit = Long.parseLong(overdraftLimitStr);

        try {
            AccountDepositoriesService.updateDepository(userId, depositoryId, accountId, bankName, accountNumber,
                    routingNumber, interestRate, overdraftLimit);
            ctx.status(200).result("Depository updated successfully");
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    public static void deleteDepository(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);
        JSONObject requestBody;
        try {
            requestBody = new JSONObject(ctx.body());
        } catch (JSONException e) {
            ctx.status(400).result(WRONG_BODY_FORMAT + e.getMessage());
            return;
        }
        String depositoryIdStr = requestBody.optString(DEPOSITORY_ID);

        if (depositoryIdStr.isEmpty()) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        UUID depositoryId = UUID.fromString(depositoryIdStr);
        try {
            AccountDepositoriesService.deleteDepository(userId, depositoryId);
            ctx.status(200).result("Depository deleted successfully");
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    public static void calculateInterest(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);
        JSONObject requestBody;
        try {
            requestBody = new JSONObject(ctx.body());
        } catch (JSONException e) {
            ctx.status(400).result(WRONG_BODY_FORMAT + e.getMessage());
            return;
        }
        String depositoryIdStr = requestBody.optString(DEPOSITORY_ID);

        if (depositoryIdStr.isEmpty()) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        UUID depositoryId = UUID.fromString(depositoryIdStr);
        try {
            AccountDepositoriesService.calculateInterest(userId, depositoryId);
            ctx.status(200).result("Interest calculated and updated successfully");
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }
}
