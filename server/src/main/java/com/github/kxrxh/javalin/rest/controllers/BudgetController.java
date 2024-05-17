package com.github.kxrxh.javalin.rest.controllers;

import com.github.kxrxh.javalin.rest.entities.BudgetAnalysisResult;
import com.github.kxrxh.javalin.rest.services.BudgetService;
import io.javalin.http.Context;

public class BudgetController {

    private static final BudgetService budgetService = new BudgetService();

    public static void setBudgetAlert(Context ctx) {
        try {
            String budgetIdStr = ctx.pathParam("budget_id");
            String alertThresholdStr = ctx.formParam("alert_threshold");

            if (alertThresholdStr == null) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            Long budgetId = Long.parseLong(budgetIdStr);
            Long alertThreshold = Long.parseLong(alertThresholdStr);

            budgetService.setBudgetAlert(budgetId, alertThreshold);
            ctx.status(200).result("Budget alert set successfully");
        } catch (Exception e) {
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void analyzeBudget(Context ctx) {
        try {
            String budgetIdStr = ctx.pathParam("budget_id");

            Long budgetId = Long.parseLong(budgetIdStr);

            BudgetAnalysisResult analysisResult = budgetService.analyzeBudget(budgetId);
            ctx.json(analysisResult);
        } catch (Exception e) {
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }
}
