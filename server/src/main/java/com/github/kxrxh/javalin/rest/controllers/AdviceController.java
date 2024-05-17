package com.github.kxrxh.javalin.rest.controllers;

import com.github.kxrxh.javalin.rest.entities.FinancialAdvice;
import com.github.kxrxh.javalin.rest.entities.FinancialForecast;
import com.github.kxrxh.javalin.rest.services.AdviceService;
import io.javalin.http.Context;

public class AdviceController {

    private static final AdviceService adviceService = new AdviceService();

    public static void getFinancialAdvice(Context ctx) {
        try {
            String userIdStr = ctx.queryParam("user_id");

            if (userIdStr == null) {
                ctx.status(400).result("Missing required parameter: user_id");
                return;
            }

            Long userId = Long.parseLong(userIdStr);

            FinancialAdvice advice = adviceService.getFinancialAdvice(userId);
            ctx.status(200).json(advice);
        } catch (Exception e) {
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void getFinancialForecast(Context ctx) {
        try {
            String userIdStr = ctx.queryParam("user_id");
            String dateRange = ctx.queryParam("date_range");

            if (userIdStr == null || dateRange == null) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            Long userId = Long.parseLong(userIdStr);

            FinancialForecast forecast = adviceService.getFinancialForecast(userId, dateRange);
            ctx.status(200).json(forecast);
        } catch (Exception e) {
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }
}
