package com.github.kxrxh.javalin.rest.controllers;

import com.github.kxrxh.javalin.rest.api.jwt.Utils;
import com.github.kxrxh.javalin.rest.database.models.AccountLoan;
import com.github.kxrxh.javalin.rest.services.AccountLoansService;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.UUID;

import org.json.JSONObject;

@Slf4j
public class AccountLoansController {

    private AccountLoansController() {
    }

    public static void createLoan(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            JSONObject requestBody = new JSONObject(ctx.body());

            String accountIdStr = requestBody.optString("account_id");
            String loanAmountStr = requestBody.optString("loan_amount");
            String outstandingBalanceStr = requestBody.optString("outstanding_balance");
            String interestRateStr = requestBody.optString("interest_rate");
            String loanTerm = requestBody.optString("loan_term");
            String dueDateStr = requestBody.optString("due_date");
            String paymentFrequency = requestBody.optString("payment_frequency");
            String collateral = requestBody.optString("collateral");

            if (accountIdStr == null || loanAmountStr == null || outstandingBalanceStr == null || interestRateStr == null || loanTerm == null || dueDateStr == null || paymentFrequency == null) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID accountId = UUID.fromString(accountIdStr);
            long loanAmount = Long.parseLong(loanAmountStr);
            long outstandingBalance = Long.parseLong(outstandingBalanceStr);
            double interestRate = Double.parseDouble(interestRateStr);
            LocalDate dueDate = LocalDate.parse(dueDateStr);

            AccountLoansService.createLoan(userId, accountId, loanAmount, outstandingBalance, interestRate, loanTerm, dueDate, paymentFrequency, collateral);
            ctx.status(200).result("Loan created successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void readLoan(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);

            String loanIdStr = ctx.queryParam("loan_id");

            if (loanIdStr == null || loanIdStr.isEmpty()) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID loanId = UUID.fromString(loanIdStr);
            AccountLoan loan = AccountLoansService.readLoan(userId, loanId);
            ctx.json(loan);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void updateLoan(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            JSONObject requestBody = new JSONObject(ctx.body());

            String loanIdStr = requestBody.optString("loan_id");
            String accountIdStr = requestBody.optString("account_id");
            String loanAmountStr = requestBody.optString("loan_amount");
            String outstandingBalanceStr = requestBody.optString("outstanding_balance");
            String interestRateStr = requestBody.optString("interest_rate");
            String loanTerm = requestBody.optString("loan_term");
            String dueDateStr = requestBody.optString("due_date");
            String paymentFrequency = requestBody.optString("payment_frequency");
            String collateral = requestBody.optString("collateral");

            if (loanIdStr == null || accountIdStr == null || loanAmountStr == null || outstandingBalanceStr == null || interestRateStr == null || loanTerm == null || dueDateStr == null || paymentFrequency == null) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID loanId = UUID.fromString(loanIdStr);
            UUID accountId = UUID.fromString(accountIdStr);
            long loanAmount = Long.parseLong(loanAmountStr);
            long outstandingBalance = Long.parseLong(outstandingBalanceStr);
            double interestRate = Double.parseDouble(interestRateStr);
            LocalDate dueDate = LocalDate.parse(dueDateStr);

            AccountLoansService.updateLoan(userId, loanId, accountId, loanAmount, outstandingBalance, interestRate, loanTerm, dueDate, paymentFrequency, collateral);
            ctx.status(200).result("Loan updated successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void deleteLoan(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            JSONObject requestBody = new JSONObject(ctx.body());

            String loanIdStr = requestBody.optString("loan_id");

            if (loanIdStr == null) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID loanId = UUID.fromString(loanIdStr);
            AccountLoansService.deleteLoan(userId, loanId);
            ctx.status(200).result("Loan deleted successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void calculateInterest(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            JSONObject requestBody = new JSONObject(ctx.body());

            String loanIdStr = requestBody.optString("loan_id");

            if (loanIdStr == null) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID loanId = UUID.fromString(loanIdStr);
            AccountLoansService.calculateInterest(userId, loanId);
            ctx.status(200).result("Interest calculated and updated successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void checkDueDateNotifications(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);

            AccountLoansService.checkDueDateNotifications(userId);
            ctx.status(200).result("Due date notifications checked");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }
}
