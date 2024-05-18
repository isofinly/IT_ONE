package com.github.kxrxh.javalin.rest.controllers;

import java.sql.Date;
import java.sql.SQLException;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import com.github.kxrxh.javalin.rest.database.models.ExchangeRate;
import com.github.kxrxh.javalin.rest.services.ExchangeRateService;

import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExchangeRateController extends AbstractController {

    private ExchangeRateController() {
    }

    public static void createExchangeRate(Context ctx) {
        JSONObject requestBody;
        try {
            requestBody = new JSONObject(ctx.body());
        } catch (JSONException e) {
            ctx.status(400).result(WRONG_BODY_FORMAT + e.getMessage());
            return;
        }
        String baseCurrency = requestBody.optString(BASE_CURRENCY);
        String convertedCurrency = requestBody.optString(CONVERTED_CURRENCY);
        String rateStr = requestBody.optString("rate");
        String dateStr = requestBody.optString("date");

        if (baseCurrency.isEmpty() || convertedCurrency.isEmpty() || rateStr.isEmpty() || dateStr.isEmpty()) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        double rate = Double.parseDouble(rateStr);
        Date date = Date.valueOf(dateStr);

        try {
            ExchangeRateService.createExchangeRate(baseCurrency, convertedCurrency, rate, date);
            ctx.status(200).result("Exchange rate created successfully");
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    public static void readExchangeRate(Context ctx) {
        String baseCurrency = ctx.queryParam(BASE_CURRENCY);
        String convertedCurrency = ctx.queryParam(CONVERTED_CURRENCY);
        String dateStr = ctx.queryParam("date");

        if (baseCurrency == null || convertedCurrency == null || dateStr == null || baseCurrency.isEmpty()
                || dateStr.isEmpty() || convertedCurrency.isEmpty()) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        Date date = Date.valueOf(dateStr);

        try {
            ExchangeRate exchangeRate = ExchangeRateService.readExchangeRate(baseCurrency, convertedCurrency, date);
            ctx.json(exchangeRate);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    public static void updateExchangeRate(Context ctx) {
        JSONObject requestBody;
        try {
            requestBody = new JSONObject(ctx.body());
        } catch (JSONException e) {
            ctx.status(400).result(WRONG_BODY_FORMAT + e.getMessage());
            return;
        }
        String idStr = requestBody.optString("id");
        String baseCurrency = requestBody.optString(BASE_CURRENCY);
        String convertedCurrency = requestBody.optString(CONVERTED_CURRENCY);
        String rateStr = requestBody.optString("rate");
        String dateStr = requestBody.optString("date");

        if (idStr.isEmpty() || baseCurrency.isEmpty() || convertedCurrency.isEmpty() || rateStr.isEmpty()
                || dateStr.isEmpty()) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        UUID id = UUID.fromString(idStr);
        double rate = Double.parseDouble(rateStr);
        Date date = Date.valueOf(dateStr);

        try {
            ExchangeRateService.updateExchangeRate(id, baseCurrency, convertedCurrency, rate, date);
            ctx.status(200).result("Exchange rate updated successfully");
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    public static void deleteExchangeRate(Context ctx) {
        JSONObject requestBody;
        try {
            requestBody = new JSONObject(ctx.body());
        } catch (JSONException e) {
            ctx.status(400).result(WRONG_BODY_FORMAT + e.getMessage());
            return;
        }
        String idStr = requestBody.optString("id");

        if (idStr.isEmpty()) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        UUID id = UUID.fromString(idStr);

        try {
            ExchangeRateService.deleteExchangeRate(id);
            ctx.status(200).result("Exchange rate deleted successfully");
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }
}
