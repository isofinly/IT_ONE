package com.github.kxrxh.javalin.rest.controllers;

import org.json.JSONObject;

import com.github.kxrxh.javalin.rest.api.jwt.Utils;
import com.github.kxrxh.javalin.rest.database.models.Tax;
import com.github.kxrxh.javalin.rest.services.TaxService;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class TaxController {

    private TaxController() {
    }

    public static void createTax(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            JSONObject requestBody = new JSONObject(ctx.body());

            String name = requestBody.optString("name");
            String description = requestBody.optString("description");
            String rateStr = requestBody.optString("rate");
            String currency = requestBody.optString("currency");

            if (name.isEmpty() || rateStr.isEmpty() || currency.isEmpty()) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            long rate = Long.parseLong(rateStr);

            TaxService.createTax(userId, name, description, rate, currency);
            ctx.status(200).result("Tax created successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void readTax(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);

            String taxIdStr = ctx.queryParam("tax_id");

            if (taxIdStr == null || taxIdStr.isEmpty()) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID taxId = UUID.fromString(taxIdStr);
            Tax tax = TaxService.readTax(userId, taxId);
            ctx.json(tax);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void updateTax(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            JSONObject requestBody = new JSONObject(ctx.body());

            String taxIdStr = requestBody.optString("tax_id");
            String name = requestBody.optString("name");
            String description = requestBody.optString("description");
            String rateStr = requestBody.optString("rate");
            String currency = requestBody.optString("currency");

            if (taxIdStr == null || name == null || rateStr == null || currency == null) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID taxId = UUID.fromString(taxIdStr);
            long rate = Long.parseLong(rateStr);

            TaxService.updateTax(userId, taxId, name, description, rate, currency);
            ctx.status(200).result("Tax updated successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void deleteTax(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            JSONObject requestBody = new JSONObject(ctx.body());

            String taxIdStr = requestBody.optString("tax_id");

            if (taxIdStr.isEmpty()) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID taxId = UUID.fromString(taxIdStr);
            TaxService.deleteTax(userId, taxId);
            ctx.status(200).result("Tax deleted successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void calculateTaxes(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);

            double totalTaxes = TaxService.calculateTaxes(userId);
            ctx.status(200).result("Total taxes: " + totalTaxes);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }
}
