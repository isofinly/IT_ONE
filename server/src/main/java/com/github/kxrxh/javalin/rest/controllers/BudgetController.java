package com.github.kxrxh.javalin.rest.controllers;

import java.util.UUID;

import com.github.kxrxh.javalin.rest.entities.BudgetAnalysisResult;
import com.github.kxrxh.javalin.rest.services.BudgetService;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;

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
            String budgetIdStr = ctx.pathParam("budget_id");

            UUID budgetId = UUID.fromString(budgetIdStr);

            BudgetAnalysisResult analysisResult = BudgetService.analyzeBudget(budgetId);
            ctx.json(analysisResult);
        } catch (Exception e) {
            log.error(e.getMessage());
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }
}
