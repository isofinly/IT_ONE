package com.github.kxrxh.javalin.rest.controllers;

import com.github.kxrxh.javalin.rest.api.jwt.Utils;
import com.github.kxrxh.javalin.rest.services.AccountService;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class AccountController {

    private AccountController() {
    }

    public static void transferFunds(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            String fromAccountId = ctx.formParam("from_account_id");
            String toAccountId = ctx.formParam("to_account_id");
            String amountStr = ctx.formParam("amount");

            if (fromAccountId == null || toAccountId == null || amountStr == null) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            long amount = Long.parseLong(amountStr);
            AccountService.transferFunds(userId, UUID.fromString(fromAccountId), UUID.fromString(toAccountId), amount);
            // TODO: Handle error from line 30
            ctx.status(200).result("Funds transferred successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void mergeAccounts(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            String[] accountIds = ctx.formParams("account_ids").toArray(new String[0]);
            String newAccountName = ctx.formParam("new_account_name");
            String accountType = ctx.formParam("account_type");

            if (accountIds.length == 0 || newAccountName == null || accountType == null) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            AccountService.mergeAccounts(userId, accountIds, newAccountName, accountType);
            ctx.status(200).result("Accounts merged successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }
}