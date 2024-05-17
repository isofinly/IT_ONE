package com.github.kxrxh.javalin.rest.controllers;

import java.util.UUID;

import com.github.kxrxh.javalin.rest.entities.FinancialAdvice;
import com.github.kxrxh.javalin.rest.entities.FinancialForecast;
import com.github.kxrxh.javalin.rest.services.AdviceService;
import com.github.kxrxh.javalin.rest.api.jwt.Utils;

import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AdviceController {

    private AdviceController() {
    }

    public static void getFinancialAdvice(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);


            FinancialAdvice advice = AdviceService.getFinancialAdvice(userId);
            ctx.status(200).json(advice);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void getFinancialForecast(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            String dateRange = ctx.queryParam("date_range");

            FinancialForecast forecast = AdviceService.getFinancialForecast(userId, dateRange);
            ctx.status(200).json(forecast);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }
}
