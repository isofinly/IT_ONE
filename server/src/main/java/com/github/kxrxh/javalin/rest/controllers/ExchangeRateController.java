package com.github.kxrxh.javalin.rest.controllers;

import org.json.JSONObject;

import com.github.kxrxh.javalin.rest.database.models.ExchangeRate;
import com.github.kxrxh.javalin.rest.services.ExchangeRateService;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;

import java.sql.Date;
import java.util.UUID;

@Slf4j
public class ExchangeRateController {

    private ExchangeRateController() {
    }

    public static void createExchangeRate(Context ctx) {
        try {
            JSONObject requestBody = new JSONObject(ctx.body());

            String baseCurrency = requestBody.optString("base_currency");
            String convertedCurrency = requestBody.optString("converted_currency");
            String rateStr = requestBody.optString("rate");
            String dateStr = requestBody.optString("date");

            if (baseCurrency.isEmpty() || convertedCurrency.isEmpty() || rateStr.isEmpty() || dateStr.isEmpty()) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            double rate = Double.parseDouble(rateStr);
            Date date = Date.valueOf(dateStr);

            ExchangeRateService.createExchangeRate(baseCurrency, convertedCurrency, rate, date);
            ctx.status(200).result("Exchange rate created successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void readExchangeRate(Context ctx) {
        try {
            String baseCurrency = ctx.queryParam("base_currency");
            String convertedCurrency = ctx.queryParam("converted_currency");
            String dateStr = ctx.queryParam("date");

            if (baseCurrency == null || convertedCurrency == null || dateStr == null || baseCurrency.isEmpty()
                    || dateStr.isEmpty() || convertedCurrency.isEmpty()) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            Date date = Date.valueOf(dateStr);
            ExchangeRate exchangeRate = ExchangeRateService.readExchangeRate(baseCurrency, convertedCurrency, date);
            ctx.json(exchangeRate);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void updateExchangeRate(Context ctx) {
        try {

            JSONObject requestBody = new JSONObject(ctx.body());

            String idStr = requestBody.optString("id");
            String baseCurrency = requestBody.optString("base_currency");
            String convertedCurrency = requestBody.optString("converted_currency");
            String rateStr = requestBody.optString("rate");
            String dateStr = requestBody.optString("date");

            if (idStr.isEmpty() || baseCurrency.isEmpty() || convertedCurrency.isEmpty() || rateStr.isEmpty()
                    || dateStr.isEmpty()) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID id = UUID.fromString(idStr);
            double rate = Double.parseDouble(rateStr);
            Date date = Date.valueOf(dateStr);

            ExchangeRateService.updateExchangeRate(id, baseCurrency, convertedCurrency, rate, date);
            ctx.status(200).result("Exchange rate updated successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void deleteExchangeRate(Context ctx) {
        try {
            JSONObject requestBody = new JSONObject(ctx.body());

            String idStr = requestBody.optString("id");

            if (idStr.isEmpty()) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID id = UUID.fromString(idStr);
            ExchangeRateService.deleteExchangeRate(id);
            ctx.status(200).result("Exchange rate deleted successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }
}
