package com.github.kxrxh.javalin.rest.controllers;

import java.sql.SQLException;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import com.github.kxrxh.javalin.rest.api.jwt.Utils;
import com.github.kxrxh.javalin.rest.services.IntegrationService;

import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IntegrationController extends AbstractController {

    public static void integrateWithBank(Context ctx) {
        
        UUID userId = Utils.getUUIDFromContext(ctx);
        String bankName;
        JSONObject requestBody;

        try {
            requestBody = new JSONObject(ctx.body());
            bankName = requestBody.optString("bank_name");
        } catch (JSONException e) {
            ctx.status(400).result(WRONG_BODY_FORMAT + e.getMessage());
            return;
        }
        // bearer token
        String bankCredentials = requestBody.optString("bank_credentials");

        try {
            IntegrationService.integrateWithBank(userId, bankName, bankCredentials);
            ctx.status(501).result("Not Implemented");
        } catch (Exception e) {
            log.error(e.getMessage());
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    public static void autoCategorizeTransactions(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);

        try {
            IntegrationService.autoCategorizeTransactions(userId);
            ctx.status(200).result("Transactions auto-categorized successfully");
        } catch (SQLException e) {
            log.error(e.getMessage());
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }
}
