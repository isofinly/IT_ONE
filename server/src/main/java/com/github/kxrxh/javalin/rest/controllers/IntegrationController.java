package com.github.kxrxh.javalin.rest.controllers;
import org.json.JSONObject;


import com.github.kxrxh.javalin.rest.api.jwt.Utils;
import com.github.kxrxh.javalin.rest.services.IntegrationService;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class IntegrationController {

    private IntegrationController() {
    }

    public static void integrateWithBank(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            
            JSONObject requestBody = new JSONObject(ctx.body());
            String bankCredentials = requestBody.optString("bank_credentials");

            IntegrationService.integrateWithBank(userId, bankCredentials);
            ctx.status(501).result("Not Implemented");
        } catch (Exception e) {
            log.error(e.getMessage());
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void autoCategorizeTransactions(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            IntegrationService.autoCategorizeTransactions(userId);
            ctx.status(200).result("Transactions auto-categorized successfully");
        } catch (Exception e) {
            log.error(e.getMessage());
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }
}
