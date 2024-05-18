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

import java.time.LocalDate;
import java.util.UUID;

import org.json.JSONObject;

@Slf4j
public class AccountBalancesController {

    private AccountBalancesController() {
    }

    public static void createBalance(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            JSONObject requestBody = new JSONObject(ctx.body());

            String accountIdStr = requestBody.optString("account_id");
            String dateStr = requestBody.optString("date");
            String balanceStr = requestBody.optString("balance");
            String currency = requestBody.optString("currency");

            if (accountIdStr.isEmpty() || dateStr.isEmpty() || balanceStr.isEmpty() || currency.isEmpty()) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID accountId = UUID.fromString(accountIdStr);
            LocalDate date = LocalDate.parse(dateStr);
            long balance = Long.parseLong(balanceStr);

            AccountBalancesService.createBalance(userId, accountId, date, balance, currency);
            ctx.status(200).result("Balance created successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void readBalance(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);

            String balanceIdStr = ctx.queryParam("balance_id");

            if (balanceIdStr == null || balanceIdStr.isEmpty()) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID balanceId = UUID.fromString(balanceIdStr);
            AccountBalance balance = AccountBalancesService.readBalance(userId, balanceId);
            ctx.json(balance);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void updateBalance(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            JSONObject requestBody = new JSONObject(ctx.body());

            String balanceIdStr = requestBody.optString("balance_id");
            String accountIdStr = requestBody.optString("account_id");
            String dateStr = requestBody.optString("date");
            String balanceStr = requestBody.optString("balance");
            String currency = requestBody.optString("currency");

            if (balanceIdStr.isEmpty() || accountIdStr.isEmpty() || dateStr.isEmpty() || balanceStr.isEmpty()
                    || currency.isEmpty()) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID balanceId = UUID.fromString(balanceIdStr);
            UUID accountId = UUID.fromString(accountIdStr);
            LocalDate date = LocalDate.parse(dateStr);
            long balance = Long.parseLong(balanceStr);

            AccountBalancesService.updateBalance(userId, balanceId, accountId, date, balance, currency);
            ctx.status(200).result("Balance updated successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void deleteBalance(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            JSONObject requestBody = new JSONObject(ctx.body());

            String balanceIdStr = requestBody.optString("balance_id");

            if (balanceIdStr.isEmpty()) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID balanceId = UUID.fromString(balanceIdStr);
            AccountBalancesService.deleteBalance(userId, balanceId);
            ctx.status(200).result("Balance deleted successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void calculateTotalBalance(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);

            String accountIdStr = ctx.queryParam("account_id");

            if (accountIdStr == null || accountIdStr.isEmpty()) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID accountId = UUID.fromString(accountIdStr);
            long totalBalance = AccountBalancesService.calculateTotalBalance(userId, accountId);
            ctx.json(new TotalBalanceResponse(totalBalance));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
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
