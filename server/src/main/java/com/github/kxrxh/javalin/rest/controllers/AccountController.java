package com.github.kxrxh.javalin.rest.controllers;

import com.github.kxrxh.javalin.rest.api.jwt.Utils;
import com.github.kxrxh.javalin.rest.services.AccountService;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.UUID;

@Slf4j
public class AccountController {

    private AccountController() {
    }

    public static void transferFunds(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            // Parse JSON data from request body
            JSONObject requestBody = new JSONObject(ctx.body());

            // Extract data from JSON
            String fromAccountId = requestBody.optString("from_account_id");
            String toAccountId = requestBody.optString("to_account_id");
            String amountStr = requestBody.optString("amount");

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

            // Parse JSON data from request body
            JSONObject requestBody = new JSONObject(ctx.body());

            // Extract data from JSON
            JSONArray accountIdsJson = requestBody.getJSONArray("account_ids");
            String[] accountIds = new String[accountIdsJson.length()];
            for (int i = 0; i < accountIdsJson.length(); i++) {
                accountIds[i] = accountIdsJson.getString(i);
            }
            String newAccountName = requestBody.optString("new_account_name");
            String accountType = requestBody.optString("account_type");

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