package com.github.kxrxh.javalin.rest.controllers;

import com.github.kxrxh.javalin.rest.services.IntegrationService;
import io.javalin.http.Context;

public class IntegrationController {

    private static final IntegrationService integrationService = new IntegrationService();

    public static void integrateWithBank(Context ctx) {
        try {
            String userIdStr = ctx.formParam("user_id");
            String bankCredentials = ctx.formParam("bank_credentials");

            if (userIdStr == null || bankCredentials == null) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            Long userId = Long.parseLong(userIdStr);

            integrationService.integrateWithBank(userId, bankCredentials);
            ctx.status(200).result("Bank integration successful");
        } catch (Exception e) {
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void autoCategorizeTransactions(Context ctx) {
        try {
            String userIdStr = ctx.formParam("user_id");

            if (userIdStr == null) {
                ctx.status(400).result("Missing required parameter: user_id");
                return;
            }

            Long userId = Long.parseLong(userIdStr);

            integrationService.autoCategorizeTransactions(userId);
            ctx.status(200).result("Transactions auto-categorized successfully");
        } catch (Exception e) {
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }
}
