package com.github.kxrxh.javalin.rest.controllers;

import com.github.kxrxh.javalin.rest.api.jwt.Utils;
import com.github.kxrxh.javalin.rest.database.models.AccountInvestment;
import com.github.kxrxh.javalin.rest.services.AccountInvestmentsService;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.UUID;

@Slf4j
public class AccountInvestmentsController extends AbstractController {

    private AccountInvestmentsController() {
    }

    public static void createInvestment(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);
        JSONObject requestBody;
        try {
            requestBody = new JSONObject(ctx.body());
        } catch (JSONException e) {
            ctx.status(400).result(WRONG_BODY_FORMAT + e.getMessage());
            return;
        }
        String accountIdStr = requestBody.optString("account_id");
        String investmentType = requestBody.optString("investment_type");
        String marketValueStr = requestBody.optString("market_value");
        String purchasePriceStr = requestBody.optString("purchase_price");
        String purchaseDateStr = requestBody.optString("purchase_date");
        String dividendsStr = requestBody.optString("dividends");
        String interestRateStr = requestBody.optString("interest_rate");

        if (accountIdStr.isEmpty() || marketValueStr.isEmpty() || purchasePriceStr.isEmpty()
                || purchaseDateStr.isEmpty() || dividendsStr.isEmpty() || interestRateStr.isEmpty()) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        UUID accountId = UUID.fromString(accountIdStr);
        long marketValue = Long.parseLong(marketValueStr);
        long purchasePrice = Long.parseLong(purchasePriceStr);
        LocalDate purchaseDate = LocalDate.parse(purchaseDateStr);
        long dividends = Long.parseLong(dividendsStr);
        double interestRate = Double.parseDouble(interestRateStr);

        try {
            AccountInvestmentsService.createInvestment(userId, accountId, investmentType, marketValue, purchasePrice,
                    purchaseDate, dividends, interestRate);
            ctx.status(200).result("Investment created successfully");
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    public static void readInvestment(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);

        String investmentIdStr = ctx.queryParam(INVESTMENT_ID);

        if (investmentIdStr == null || investmentIdStr.isEmpty()) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        UUID investmentId = UUID.fromString(investmentIdStr);
        try {
            AccountInvestment investment = AccountInvestmentsService.readInvestment(userId, investmentId);
            ctx.json(investment);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    public static void updateInvestment(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);
        JSONObject requestBody;
        try {
            requestBody = new JSONObject(ctx.body());
        } catch (JSONException e) {
            ctx.status(400).result(WRONG_BODY_FORMAT + e.getMessage());
            return;
        }
        String investmentIdStr = requestBody.optString("investment_id");
        String accountIdStr = requestBody.optString("account_id");
        String investmentType = requestBody.optString("investment_type");
        String marketValueStr = requestBody.optString("market_value");
        String purchasePriceStr = requestBody.optString("purchase_price");
        String purchaseDateStr = requestBody.optString("purchase_date");
        String dividendsStr = requestBody.optString("dividends");
        String interestRateStr = requestBody.optString("interest_rate");

        if (investmentIdStr.isEmpty() || accountIdStr.isEmpty() || marketValueStr.isEmpty()
                || purchasePriceStr.isEmpty() || purchaseDateStr.isEmpty() || dividendsStr.isEmpty()
                || interestRateStr.isEmpty()) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        UUID investmentId = UUID.fromString(investmentIdStr);
        UUID accountId = UUID.fromString(accountIdStr);
        long marketValue = Long.parseLong(marketValueStr);
        long purchasePrice = Long.parseLong(purchasePriceStr);
        LocalDate purchaseDate = LocalDate.parse(purchaseDateStr);
        long dividends = Long.parseLong(dividendsStr);
        double interestRate = Double.parseDouble(interestRateStr);

        try {
            AccountInvestmentsService.updateInvestment(userId, investmentId, accountId, investmentType, marketValue,
                    purchasePrice, purchaseDate, dividends, interestRate);
            ctx.status(200).result("Investment updated successfully");
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    public static void deleteInvestment(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);
        JSONObject requestBody;
        try {
            requestBody = new JSONObject(ctx.body());
        } catch (JSONException e) {
            ctx.status(400).result(WRONG_BODY_FORMAT + e.getMessage());
            return;
        }
        String investmentIdStr = requestBody.optString(INVESTMENT_ID);

        if (investmentIdStr.isEmpty()) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        UUID investmentId = UUID.fromString(investmentIdStr);
        try {
            AccountInvestmentsService.deleteInvestment(userId, investmentId);
            ctx.status(200).result("Investment deleted successfully");
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    public static void calculateDividends(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);

        String investmentIdStr = ctx.queryParam(INVESTMENT_ID);

        if (investmentIdStr.isEmpty()) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        UUID investmentId = UUID.fromString(investmentIdStr);

        try {
            AccountInvestmentsService.calculateDividends(userId, investmentId);
            ctx.status(200).result("Dividends calculated and updated successfully");
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }
}
