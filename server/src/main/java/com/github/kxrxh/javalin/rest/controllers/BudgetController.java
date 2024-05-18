package com.github.kxrxh.javalin.rest.controllers;

import org.json.JSONObject;

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
            JSONObject requestBody = new JSONObject(ctx.body());
            String budgetIdStr = requestBody.optString("budget_id");
            String alertThresholdStr = requestBody.optString("alert_threshold");

            if (budgetIdStr.isEmpty() || alertThresholdStr.isEmpty()) {
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
            UUID userId = Utils.getUUIDFromContext(ctx);

            String budgetIdStr = ctx.queryParam("budget_id");
            if (budgetIdStr == null || budgetIdStr.isEmpty()) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            String dateRange = ctx.queryParam("date_range");
            if (dateRange == null || dateRange.isEmpty()) {
                ctx.status(400).result("Missing required parameters");
                return;
            }
            UUID budgetId = UUID.fromString(budgetIdStr);

            BudgetAnalysisResult analysisResult = BudgetService.analyzeBudget(userId, budgetId, dateRange);
            ctx.json(analysisResult);
        } catch (Exception e) {
            log.error(e.getMessage());
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }
}
