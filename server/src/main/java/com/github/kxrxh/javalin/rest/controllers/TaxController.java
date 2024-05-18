package com.github.kxrxh.javalin.rest.controllers;

import com.github.kxrxh.javalin.rest.api.jwt.Utils;
import com.github.kxrxh.javalin.rest.services.TaxService;
import io.javalin.http.Context;
import com.github.kxrxh.javalin.rest.database.models.Tax;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TaxController {

    private TaxController() {
    }

    public static void createTax(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            String name = ctx.formParam("name");
            String description = ctx.formParam("description");
            String rateStr = ctx.formParam("rate");
            String currency = ctx.formParam("currency");

            if (name == null || rateStr == null || currency == null) {
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
            String taxIdStr = ctx.pathParam("tax_id");

            if (taxIdStr == null) {
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
            String taxIdStr = ctx.pathParam("tax_id");
            String name = ctx.formParam("name");
            String description = ctx.formParam("description");
            String rateStr = ctx.formParam("rate");
            String currency = ctx.formParam("currency");

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
            String taxIdStr = ctx.pathParam("tax_id");

            if (taxIdStr == null) {
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
