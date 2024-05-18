package com.github.kxrxh.javalin.rest.controllers;

import com.github.kxrxh.javalin.rest.api.jwt.Utils;
import com.github.kxrxh.javalin.rest.database.models.AccountBalance;
import com.github.kxrxh.javalin.rest.services.AccountBalancesService;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
public class AccountBalancesController {

    private AccountBalancesController() {
    }

    public static void createBalance(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            String accountIdStr = ctx.formParam("account_id");
            String dateStr = ctx.formParam("date");
            String balanceStr = ctx.formParam("balance");
            String currency = ctx.formParam("currency");

            if (accountIdStr == null || dateStr == null || balanceStr == null || currency == null) {
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
            String balanceIdStr = ctx.pathParam("balance_id");

            if (balanceIdStr == null) {
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
            String balanceIdStr = ctx.pathParam("balance_id");
            String accountIdStr = ctx.formParam("account_id");
            String dateStr = ctx.formParam("date");
            String balanceStr = ctx.formParam("balance");
            String currency = ctx.formParam("currency");

            if (balanceIdStr == null || accountIdStr == null || dateStr == null || balanceStr == null || currency == null) {
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
            String balanceIdStr = ctx.pathParam("balance_id");

            if (balanceIdStr == null) {
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

            if (accountIdStr == null) {
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

    static class TotalBalanceResponse {
        public long totalBalance;

        public TotalBalanceResponse(long totalBalance) {
            this.totalBalance = totalBalance;
        }
    }
}
