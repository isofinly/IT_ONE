package com.github.kxrxh.javalin.rest.controllers;

import com.github.kxrxh.javalin.rest.api.jwt.Utils;
import com.github.kxrxh.javalin.rest.services.AccountService;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.UUID;

@Slf4j
public class AccountController extends AbstractController {

    public static void transferFunds(Context ctx) {
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
        String fromAccountId = requestBody.optString("from_account_id");
        String toAccountId = requestBody.optString("to_account_id");
        String amountStr = requestBody.optString("amount");

        if (fromAccountId.isEmpty() || toAccountId.isEmpty() || amountStr.isEmpty()) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }
        try {
            long amount = Long.parseLong(amountStr);
            AccountService.transferFunds(userId, UUID.fromString(fromAccountId), UUID.fromString(toAccountId), amount);
        } catch (NumberFormatException e) {
            log.error(e.getMessage());
            ctx.status(400).result("Invalid parameters: " + e.getMessage());
            return;
        } catch (SQLException e) {
            log.error(e.getMessage());
            ctx.status(500).result("Could not transfer funds: " + e.getMessage());
            return;
        }

        ctx.status(200).result("Funds transferred successfully");

    }

    public static void mergeAccounts(Context ctx) {
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
        JSONArray accountIdsJson;
        try {
            accountIdsJson = requestBody.getJSONArray("account_ids");
        } catch (JSONException e) {
            ctx.status(400).result("Unable to parse account_ids: " + e.getMessage());
            return;
        }

        String[] accountIds = new String[accountIdsJson.length()];
        for (int i = 0; i < accountIdsJson.length(); i++) {
            accountIds[i] = accountIdsJson.getString(i);
        }

        String newAccountName = requestBody.optString("new_account_name");
        String accountType = requestBody.optString("account_type");

        if (accountIds.length == 0 || newAccountName.isEmpty() || accountType.isEmpty()) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        try {
            AccountService.mergeAccounts(userId, accountIds, newAccountName, accountType);
        } catch (SQLException e) {
            log.error(e.getMessage());
            ctx.status(500).result("Could not merge accounts: " + e.getMessage());
            return;
        }

        ctx.status(200).result("Accounts merged successfully");
    }
}