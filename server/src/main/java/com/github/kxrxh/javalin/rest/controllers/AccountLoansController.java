package com.github.kxrxh.javalin.rest.controllers;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import com.github.kxrxh.javalin.rest.api.jwt.Utils;
import com.github.kxrxh.javalin.rest.database.models.AccountLoan;
import com.github.kxrxh.javalin.rest.services.AccountLoansService;

import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccountLoansController extends AbstractController {

    private AccountLoansController() {
    }

    public static void createLoan(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);
        JSONObject requestBody;
        try {
            requestBody = new JSONObject(ctx.body());
        } catch (JSONException e) {
            ctx.status(400).result(WRONG_BODY_FORMAT + e.getMessage());
            return;
        }
        String accountIdStr = requestBody.optString("account_id");
        String loanAmountStr = requestBody.optString("loan_amount");
        String outstandingBalanceStr = requestBody.optString("outstanding_balance");
        String interestRateStr = requestBody.optString("interest_rate");
        String loanTerm = requestBody.optString("loan_term");
        String dueDateStr = requestBody.optString("due_date");
        String paymentFrequency = requestBody.optString("payment_frequency");
        String collateral = requestBody.optString("collateral");

        if (accountIdStr == null || loanAmountStr == null || outstandingBalanceStr == null
                || interestRateStr == null || loanTerm == null || dueDateStr == null || paymentFrequency == null) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        UUID accountId = UUID.fromString(accountIdStr);
        long loanAmount = Long.parseLong(loanAmountStr);
        long outstandingBalance = Long.parseLong(outstandingBalanceStr);
        double interestRate = Double.parseDouble(interestRateStr);
        LocalDate dueDate = LocalDate.parse(dueDateStr);

        try {
            AccountLoansService.createLoan(userId, accountId, loanAmount, outstandingBalance, interestRate, loanTerm,
                    dueDate, paymentFrequency, collateral);
            ctx.status(200).result("Loan created successfully");
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    public static void readLoan(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);

        String loanIdStr = ctx.queryParam(LOAN_ID);
        if (loanIdStr == null || loanIdStr.isEmpty()) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        UUID loanId = UUID.fromString(loanIdStr);

        try {
            AccountLoan loan = AccountLoansService.readLoan(userId, loanId);
            ctx.json(loan);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    public static void updateLoan(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);
        JSONObject requestBody;
        try {
            requestBody = new JSONObject(ctx.body());
        } catch (JSONException e) {
            ctx.status(400).result(WRONG_BODY_FORMAT + e.getMessage());
            return;
        }
        String loanIdStr = requestBody.optString("loan_id");
        String accountIdStr = requestBody.optString("account_id");
        String loanAmountStr = requestBody.optString("loan_amount");
        String outstandingBalanceStr = requestBody.optString("outstanding_balance");
        String interestRateStr = requestBody.optString("interest_rate");
        String loanTerm = requestBody.optString("loan_term");
        String dueDateStr = requestBody.optString("due_date");
        String paymentFrequency = requestBody.optString("payment_frequency");
        String collateral = requestBody.optString("collateral");

        if (loanIdStr == null || accountIdStr == null || loanAmountStr == null || outstandingBalanceStr == null
                || interestRateStr == null || loanTerm == null || dueDateStr == null || paymentFrequency == null) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        UUID loanId = UUID.fromString(loanIdStr);
        UUID accountId = UUID.fromString(accountIdStr);
        long loanAmount = Long.parseLong(loanAmountStr);
        long outstandingBalance = Long.parseLong(outstandingBalanceStr);
        double interestRate = Double.parseDouble(interestRateStr);
        LocalDate dueDate = LocalDate.parse(dueDateStr);

        try {
            AccountLoansService.updateLoan(userId, loanId, accountId, loanAmount, outstandingBalance, interestRate,
                    loanTerm, dueDate, paymentFrequency, collateral);
            ctx.status(200).result("Loan updated successfully");
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    public static void deleteLoan(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);
        JSONObject requestBody;
        try {
            requestBody = new JSONObject(ctx.body());
        } catch (JSONException e) {
            ctx.status(400).result(WRONG_BODY_FORMAT + e.getMessage());
            return;
        }
        String loanIdStr = requestBody.optString(LOAN_ID);

        if (loanIdStr == null) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        UUID loanId = UUID.fromString(loanIdStr);
        try {
            AccountLoansService.deleteLoan(userId, loanId);
            ctx.status(200).result("Loan deleted successfully");
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    public static void calculateInterest(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);
        JSONObject requestBody;
        try {
            requestBody = new JSONObject(ctx.body());
        } catch (JSONException e) {
            ctx.status(400).result(WRONG_BODY_FORMAT + e.getMessage());
            return;
        }
        String loanIdStr = requestBody.optString(LOAN_ID);

        if (loanIdStr == null) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        UUID loanId = UUID.fromString(loanIdStr);

        try {
            AccountLoansService.calculateInterest(userId, loanId);
            ctx.status(200).result("Interest calculated and updated successfully");
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    public static void checkDueDateNotifications(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);

        try {
            AccountLoansService.checkDueDateNotifications(userId);
            ctx.status(200).result("Due date notifications checked");
            // todo: replace with actual implementation
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }
}
