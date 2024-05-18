package com.github.kxrxh.javalin.rest.controllers;

import java.time.LocalDate;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import com.github.kxrxh.javalin.rest.api.jwt.Utils;
import com.github.kxrxh.javalin.rest.database.models.Valuation;
import com.github.kxrxh.javalin.rest.services.ValuationService;

import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValuationController extends AbstractController {

    private ValuationController() {
    }

    public static void createValuation(Context ctx) {

        UUID userId = Utils.getUUIDFromContext(ctx);
        JSONObject requestBody;
        try {
            requestBody = new JSONObject(ctx.body());
        } catch (JSONException e) {
            ctx.status(400).result(WRONG_BODY_FORMAT + e.getMessage());
            return;
        }
        String accountIdStr = requestBody.optString("account_id");
        String dateStr = requestBody.optString("date");
        String valueStr = requestBody.optString("value");
        String currency = requestBody.optString("currency");

        if (accountIdStr.isEmpty() || dateStr.isEmpty() || valueStr.isEmpty() || currency.isEmpty()) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        UUID accountId = UUID.fromString(accountIdStr);
        LocalDate date = LocalDate.parse(dateStr);
        long value = Long.parseLong(valueStr);
        try {
            ValuationService.createValuation(userId, accountId, date, value, currency);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
            return;
        }
        ctx.status(200).result("Valuation created successfully");
    }

    public static void readValuation(Context ctx) {

        UUID userId = Utils.getUUIDFromContext(ctx);

        String valuationIdStr = ctx.queryParam(VALUATION_ID);
        String targetCurrency = ctx.queryParam("target_currency");

        if (valuationIdStr == null || targetCurrency == null) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        UUID valuationId = UUID.fromString(valuationIdStr);
        try {
            Valuation valuation = ValuationService.readValuation(userId, valuationId, targetCurrency);
            ctx.json(valuation);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }

    }

    public static void updateValuation(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);
        JSONObject requestBody;
        try {
            requestBody = new JSONObject(ctx.body());
        } catch (JSONException e) {
            ctx.status(400).result(WRONG_BODY_FORMAT + e.getMessage());
            return;
        }
        String valuationIdStr = requestBody.optString(VALUATION_ID);
        String accountIdStr = requestBody.optString("account_id");
        String dateStr = requestBody.optString("date");
        String valueStr = requestBody.optString("value");
        String currency = requestBody.optString("currency");

        if (valuationIdStr.isEmpty() || accountIdStr.isEmpty() || dateStr.isEmpty() || valueStr.isEmpty()
                || currency.isEmpty()) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        UUID valuationId = UUID.fromString(valuationIdStr);
        UUID accountId = UUID.fromString(accountIdStr);
        LocalDate date = LocalDate.parse(dateStr);
        long value = Long.parseLong(valueStr);
        try {
            ValuationService.updateValuation(userId, valuationId, accountId, date, value, currency);
            ctx.status(200).result("Valuation updated successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    public static void deleteValuation(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);
        JSONObject requestBody;
        try {
            requestBody = new JSONObject(ctx.body());
        } catch (JSONException e) {
            ctx.status(400).result(WRONG_BODY_FORMAT + e.getMessage());
            return;
        }
        String valuationIdStr = requestBody.optString(VALUATION_ID);

        if (valuationIdStr.isEmpty()) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        UUID valuationId = UUID.fromString(valuationIdStr);
        try {
            ValuationService.deleteValuation(userId, valuationId);
            ctx.status(200).result("Valuation deleted successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

}
