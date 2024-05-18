package com.github.kxrxh.javalin.rest.controllers;

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
            String bankCredentials = ctx.formParam("bank_credentials");

            IntegrationService.integrateWithBank(userId, bankCredentials);
            ctx.status(200).result("Bank integration successful");
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
