package com.github.kxrxh.javalin.rest.controllers;

import com.github.kxrxh.javalin.rest.api.jwt.Utils;
import com.github.kxrxh.javalin.rest.database.models.AccountInvestment;
import com.github.kxrxh.javalin.rest.services.AccountInvestmentsService;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.UUID;

import org.json.JSONObject;

@Slf4j
public class AccountInvestmentsController {

    private AccountInvestmentsController() {
    }

    public static void createInvestment(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            JSONObject requestBody = new JSONObject(ctx.body());

            String accountIdStr = requestBody.optString("account_id");
            String investmentType = requestBody.optString("investment_type");
            String marketValueStr = requestBody.optString("market_value");
            String purchasePriceStr = requestBody.optString("purchase_price");
            String purchaseDateStr = requestBody.optString("purchase_date");
            String dividendsStr = requestBody.optString("dividends");
            String interestRateStr = requestBody.optString("interest_rate");

            if (accountIdStr.isEmpty() || marketValueStr.isEmpty() || purchasePriceStr.isEmpty()
                    || purchaseDateStr.isEmpty() || dividendsStr.isEmpty() || interestRateStr.isEmpty()) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID accountId = UUID.fromString(accountIdStr);
            long marketValue = Long.parseLong(marketValueStr);
            long purchasePrice = Long.parseLong(purchasePriceStr);
            LocalDate purchaseDate = LocalDate.parse(purchaseDateStr);
            long dividends = Long.parseLong(dividendsStr);
            double interestRate = Double.parseDouble(interestRateStr);

            AccountInvestmentsService.createInvestment(userId, accountId, investmentType, marketValue, purchasePrice,
                    purchaseDate, dividends, interestRate);
            ctx.status(200).result("Investment created successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void readInvestment(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);

            String investmentIdStr = ctx.queryParam("investment_id");

            if (investmentIdStr == null || investmentIdStr.isEmpty()) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID investmentId = UUID.fromString(investmentIdStr);
            AccountInvestment investment = AccountInvestmentsService.readInvestment(userId, investmentId);
            ctx.json(investment);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void updateInvestment(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            JSONObject requestBody = new JSONObject(ctx.body());

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
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID investmentId = UUID.fromString(investmentIdStr);
            UUID accountId = UUID.fromString(accountIdStr);
            long marketValue = Long.parseLong(marketValueStr);
            long purchasePrice = Long.parseLong(purchasePriceStr);
            LocalDate purchaseDate = LocalDate.parse(purchaseDateStr);
            long dividends = Long.parseLong(dividendsStr);
            double interestRate = Double.parseDouble(interestRateStr);

            AccountInvestmentsService.updateInvestment(userId, investmentId, accountId, investmentType, marketValue,
                    purchasePrice, purchaseDate, dividends, interestRate);
            ctx.status(200).result("Investment updated successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void deleteInvestment(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            JSONObject requestBody = new JSONObject(ctx.body());

            String investmentIdStr = requestBody.optString("investment_id");

            if (investmentIdStr.isEmpty()) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID investmentId = UUID.fromString(investmentIdStr);
            AccountInvestmentsService.deleteInvestment(userId, investmentId);
            ctx.status(200).result("Investment deleted successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void calculateDividends(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            JSONObject requestBody = new JSONObject(ctx.body());

            String investmentIdStr = requestBody.optString("investment_id");

            if (investmentIdStr.isEmpty()) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID investmentId = UUID.fromString(investmentIdStr);
            AccountInvestmentsService.calculateDividends(userId, investmentId);
            ctx.status(200).result("Dividends calculated and updated successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }
}
