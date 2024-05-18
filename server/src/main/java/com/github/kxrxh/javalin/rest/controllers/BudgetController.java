package com.github.kxrxh.javalin.rest.controllers;

import com.github.kxrxh.javalin.rest.api.jwt.Utils;
import com.github.kxrxh.javalin.rest.entities.BudgetAnalysisResult;
import com.github.kxrxh.javalin.rest.services.BudgetService;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class BudgetController {
    private BudgetController() {
    }

    public static void setBudgetAlert(Context ctx) {
        try {
            String budgetIdStr = ctx.pathParam("budget_id");
            String alertThresholdStr = ctx.formParam("alert_threshold");

            if (alertThresholdStr == null) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID budgetId = UUID.fromString(budgetIdStr);
            Long alertThreshold = Long.parseLong(alertThresholdStr);

            BudgetService.setBudgetAlert(budgetId, alertThreshold);
            ctx.status(200).result("Budget alert set successfully");
        } catch (Exception e) {
            log.error(e.getMessage());
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void analyzeBudget(Context ctx) {
        try {
            UUID budgetId = UUID.fromString(ctx.pathParam("budget_id"));
            String dateRange = ctx.queryParam("date_range");
            UUID userId = Utils.getUUIDFromContext(ctx);

            if (dateRange == null) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            BudgetAnalysisResult analysisResult = BudgetService.analyzeBudget(userId, budgetId, dateRange);
            ctx.json(analysisResult);
        } catch (Exception e) {
            log.error(e.getMessage());
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }
}
