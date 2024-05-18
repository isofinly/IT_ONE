package com.github.kxrxh.javalin.rest.controllers;

import com.github.kxrxh.javalin.rest.api.jwt.Utils;
import com.github.kxrxh.javalin.rest.database.models.AccountBalance;
import com.github.kxrxh.javalin.rest.services.AccountBalancesService;
import io.javalin.http.Context;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.UUID;

@Slf4j
public class AccountBalancesController extends AbstractController {

    private AccountBalancesController() {
    }

    public static void createBalance(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);
        JSONObject requestBody;
        try {
            requestBody = new JSONObject(ctx.body());
        } catch (JSONException e) {
            ctx.status(400).result(WRONG_BODY_FORMAT + e.getMessage());
            return;
        }
        String accountIdStr = requestBody.optString(ACCOUNT_ID);
        String dateStr = requestBody.optString("date");
        String balanceStr = requestBody.optString("balance");
        String currency = requestBody.optString("currency");

        if (accountIdStr.isEmpty() || dateStr.isEmpty() || balanceStr.isEmpty() || currency.isEmpty()) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        UUID accountId = UUID.fromString(accountIdStr);
        try {
            LocalDate date = LocalDate.parse(dateStr);
            long balance = Long.parseLong(balanceStr);

            AccountBalancesService.createBalance(userId, accountId, date, balance, currency);
            ctx.status(200).result("Balance created successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    public static void readBalance(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);

        String balanceIdStr = ctx.queryParam(BALANCE_ID);

        if (balanceIdStr == null || balanceIdStr.isEmpty()) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        try {
            UUID balanceId = UUID.fromString(balanceIdStr);
            AccountBalance balance = AccountBalancesService.readBalance(userId, balanceId);
            ctx.json(balance);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    public static void updateBalance(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);
        JSONObject requestBody;
        try {
            requestBody = new JSONObject(ctx.body());
        } catch (JSONException e) {
            ctx.status(400).result(WRONG_BODY_FORMAT + e.getMessage());
            return;
        }
        String balanceIdStr = requestBody.optString(BALANCE_ID);
        String accountIdStr = requestBody.optString(ACCOUNT_ID);
        String dateStr = requestBody.optString("date");
        String balanceStr = requestBody.optString("balance");
        String currency = requestBody.optString("currency");

        if (balanceIdStr.isEmpty() || accountIdStr.isEmpty() || dateStr.isEmpty() || balanceStr.isEmpty()
                || currency.isEmpty()) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        UUID balanceId = UUID.fromString(balanceIdStr);
        UUID accountId = UUID.fromString(accountIdStr);

        try {
            LocalDate date = LocalDate.parse(dateStr);
            long balance = Long.parseLong(balanceStr);

            AccountBalancesService.updateBalance(userId, balanceId, accountId, date, balance, currency);
            ctx.status(200).result("Balance updated successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    public static void deleteBalance(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);
        JSONObject requestBody;
        try {
            requestBody = new JSONObject(ctx.body());
        } catch (JSONException e) {
            ctx.status(400).result(WRONG_BODY_FORMAT + e.getMessage());
            return;
        }
        String balanceIdStr = requestBody.optString(BALANCE_ID);

        if (balanceIdStr.isEmpty()) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        UUID balanceId = UUID.fromString(balanceIdStr);
        try {
            AccountBalancesService.deleteBalance(userId, balanceId);
            ctx.status(200).result("Balance deleted successfully");
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    public static void calculateTotalBalance(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);

        try {
            long totalBalance = AccountBalancesService.calculateTotalBalance(userId);
            ctx.json(new TotalBalanceResponse(totalBalance));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class TotalBalanceResponse {
        private long totalBalance;
    }
}
