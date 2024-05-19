package com.github.kxrxh.javalin.rest.controllers;

import com.github.kxrxh.javalin.rest.api.jwt.Utils;
import com.github.kxrxh.javalin.rest.database.models.Tax;
import com.github.kxrxh.javalin.rest.services.TaxService;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

@Slf4j
public class TaxController extends AbstractController {

    private TaxController() {
    }

    public static void createTax(Context ctx) {

        JSONObject requestBody;
        try {
            requestBody = new JSONObject(ctx.body());
        } catch (JSONException e) {
            ctx.status(400).result(WRONG_BODY_FORMAT + e.getMessage());
            return;
        }
        String name = requestBody.optString("name");
        String description = requestBody.optString("description");
        String rateStr = requestBody.optString("rate");
        String currency = requestBody.optString("currency");

        if (name.isEmpty() || rateStr.isEmpty() || currency.isEmpty()) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        long rate = Long.parseLong(rateStr);

        try {
            TaxService.createTax(name, description, rate, currency);
            ctx.status(200).result("Tax created successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    public static void readTax(Context ctx) {
        String taxIdStr = ctx.queryParam(TAX_ID);

        if (taxIdStr == null || taxIdStr.isEmpty()) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        UUID taxId = UUID.fromString(taxIdStr);
        try {
            Tax tax = TaxService.readTax(taxId);
            ctx.json(tax);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    public static void updateTax(Context ctx) {
        JSONObject requestBody;
        try {
            requestBody = new JSONObject(ctx.body());
        } catch (JSONException e) {
            ctx.status(400).result(WRONG_BODY_FORMAT + e.getMessage());
            return;
        }
        String taxIdStr = requestBody.optString(TAX_ID);
        String name = requestBody.optString("name");
        String description = requestBody.optString("description");
        String rateStr = requestBody.optString("rate");
        String currency = requestBody.optString("currency");

        if (taxIdStr == null || name == null || rateStr == null || currency == null) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        UUID taxId = UUID.fromString(taxIdStr);
        long rate = Long.parseLong(rateStr);

        try {
            TaxService.updateTax(taxId, name, description, rate, currency);
            ctx.status(200).result("Tax updated successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    public static void deleteTax(Context ctx) {
        JSONObject requestBody;
        try {
            requestBody = new JSONObject(ctx.body());
        } catch (JSONException e) {
            ctx.status(400).result(WRONG_BODY_FORMAT + e.getMessage());
            return;
        }
        String taxIdStr = requestBody.optString(TAX_ID);

        if (taxIdStr.isEmpty()) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        UUID taxId = UUID.fromString(taxIdStr);
        try {
            TaxService.deleteTax(taxId);
            ctx.status(200).result("Tax deleted successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    public static void calculateTaxes(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);

        try {
            double totalTaxes = TaxService.calculateTaxes(userId);
            ctx.status(200).result("Total taxes: " + totalTaxes);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }
}
