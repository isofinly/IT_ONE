package com.github.kxrxh.javalin.rest.controllers;

import java.sql.SQLException;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import com.github.kxrxh.javalin.rest.api.jwt.Utils;
import com.github.kxrxh.javalin.rest.entities.BudgetAnalysisResult;
import com.github.kxrxh.javalin.rest.services.BudgetService;

import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BudgetController extends AbstractController {

    public static void setBudgetAlert(Context ctx) {
        JSONObject requestBody;
        try {
            requestBody = new JSONObject(ctx.body());
        } catch (JSONException e) {
            ctx.status(400).result(WRONG_BODY_FORMAT + e.getMessage());
            return;
        }
        String budgetIdStr = requestBody.optString("budget_id");
        String alertThresholdStr = requestBody.optString("alert_threshold");

        if (budgetIdStr.isEmpty() || alertThresholdStr.isEmpty()) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        UUID budgetId = UUID.fromString(budgetIdStr);
        long alertThreshold = Long.parseLong(alertThresholdStr);

        try {
            BudgetService.setBudgetAlert(budgetId, alertThreshold);
            ctx.status(200).result("Budget alert set successfully");
        } catch (SQLException e) {
            log.error(e.getMessage());
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    public static void analyzeBudget(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);

        String budgetIdStr = ctx.queryParam("budget_id");
        if (budgetIdStr == null || budgetIdStr.isEmpty()) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        String dateRange = ctx.queryParam("date_range");
        if (dateRange == null || dateRange.isEmpty()) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        dateRange = dateRange.replace("_", " ");
        UUID budgetId = UUID.fromString(budgetIdStr);

        try {
            BudgetAnalysisResult analysisResult = BudgetService.analyzeBudget(userId, budgetId, dateRange);
            ctx.json(analysisResult);
        } catch (SQLException e) {
            log.error(e.getMessage());
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }
}
