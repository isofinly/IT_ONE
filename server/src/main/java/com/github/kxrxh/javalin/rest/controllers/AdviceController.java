package com.github.kxrxh.javalin.rest.controllers;

import java.sql.SQLException;
import java.util.UUID;

import com.github.kxrxh.javalin.rest.api.jwt.Utils;
import com.github.kxrxh.javalin.rest.entities.FinancialAdvice;
import com.github.kxrxh.javalin.rest.entities.FinancialForecast;
import com.github.kxrxh.javalin.rest.services.AdviceService;

import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AdviceController extends AbstractController {

    public static void getFinancialAdvice(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);

        try {
            FinancialAdvice advice = AdviceService.getFinancialAdvice(userId);
            ctx.status(200).json(advice);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    public static void getFinancialForecast(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);
        String dateRange = ctx.queryParam("date_range");
        if (dateRange == null || dateRange.isEmpty()) {
            ctx.status(400).result("Bad Request: date_range parameter is required");
            return;
        }

        dateRange = dateRange.replace("_", " ");

        try {
            FinancialForecast forecast = AdviceService.getFinancialForecast(userId, dateRange);
            ctx.status(200).json(forecast);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }
}
