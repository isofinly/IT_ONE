package com.github.kxrxh.javalin.rest.controllers;

import java.util.UUID;

import com.github.kxrxh.javalin.rest.services.IntegrationService;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IntegrationController {

    private IntegrationController() {
    }

    public static void integrateWithBank(Context ctx) {
        try {
            String userIdStr = ctx.formParam("user_id");
            String bankCredentials = ctx.formParam("bank_credentials");

            if (userIdStr == null || bankCredentials == null) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID userId = UUID.fromString(userIdStr);

            IntegrationService.integrateWithBank(userId, bankCredentials);
            ctx.status(200).result("Bank integration successful");
        } catch (Exception e) {
            log.error(e.getMessage());
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

            UUID userId = UUID.fromString(userIdStr);

            IntegrationService.autoCategorizeTransactions(userId);
            ctx.status(200).result("Transactions auto-categorized successfully");
        } catch (Exception e) {
            log.error(e.getMessage());
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }
}
