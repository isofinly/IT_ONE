package com.github.kxrxh.javalin.rest.controllers;

import org.json.JSONObject;

import com.github.kxrxh.javalin.rest.api.jwt.Utils;
import com.github.kxrxh.javalin.rest.database.models.Valuation;
import com.github.kxrxh.javalin.rest.services.ValuationService;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
public class ValuationController {

    private ValuationController() {
    }

    public static void createValuation(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            JSONObject requestBody = new JSONObject(ctx.body());

            String accountIdStr = requestBody.optString("account_id");
            String dateStr = requestBody.optString("date");
            String valueStr = requestBody.optString("value");
            String currency = requestBody.optString("currency");

            if (accountIdStr.isEmpty() || dateStr.isEmpty() || valueStr.isEmpty() || currency.isEmpty()) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID accountId = UUID.fromString(accountIdStr);
            LocalDate date = LocalDate.parse(dateStr);
            long value = Long.parseLong(valueStr);

            ValuationService.createValuation(userId, accountId, date, value, currency);
            ctx.status(200).result("Valuation created successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void readValuation(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);

            String valuationIdStr = ctx.queryParam("valuation_id");
            String targetCurrency = ctx.queryParam("target_currency");

            if (valuationIdStr == null || targetCurrency == null) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID valuationId = UUID.fromString(valuationIdStr);
            Valuation valuation = ValuationService.readValuation(userId, valuationId, targetCurrency);
            ctx.json(valuation);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void updateValuation(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            JSONObject requestBody = new JSONObject(ctx.body());

            String valuationIdStr = requestBody.optString("valuation_id");
            String accountIdStr = requestBody.optString("account_id");
            String dateStr = requestBody.optString("date");
            String valueStr = requestBody.optString("value");
            String currency = requestBody.optString("currency");

            if (valuationIdStr.isEmpty() || accountIdStr.isEmpty() || dateStr.isEmpty() || valueStr.isEmpty()
                    || currency.isEmpty()) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID valuationId = UUID.fromString(valuationIdStr);
            UUID accountId = UUID.fromString(accountIdStr);
            LocalDate date = LocalDate.parse(dateStr);
            long value = Long.parseLong(valueStr);

            ValuationService.updateValuation(userId, valuationId, accountId, date, value, currency);
            ctx.status(200).result("Valuation updated successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void deleteValuation(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            JSONObject requestBody = new JSONObject(ctx.body());

            String valuationIdStr = requestBody.optString("valuation_id");

            if (valuationIdStr.isEmpty()) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID valuationId = UUID.fromString(valuationIdStr);
            ValuationService.deleteValuation(userId, valuationId);
            ctx.status(200).result("Valuation deleted successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

}
