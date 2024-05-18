package com.github.kxrx.accountcredits;
import java.util.UUID;

import com.github.kxrxh.javalin.rest.api.jwt.Utils;

import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccountCreditsController {

    private AccountCreditsController() {
    }

    public static void createCredit(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            String accountIdStr = ctx.formParam("account_id");
            String creditLimitStr = ctx.formParam("credit_limit");
            String interestRateStr = ctx.formParam("interest_rate");
            String dueDateStr = ctx.formParam("due_date");
            String minimumPaymentStr = ctx.formParam("minimum_payment");

            if (accountIdStr == null || creditLimitStr == null || interestRateStr == null || dueDateStr == null || minimumPaymentStr == null) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID accountId = UUID.fromString(accountIdStr);
            long creditLimit = Long.parseLong(creditLimitStr);
            double interestRate = Double.parseDouble(interestRateStr);
            LocalDate dueDate = LocalDate.parse(dueDateStr);
            long minimumPayment = Long.parseLong(minimumPaymentStr);

            AccountCreditsService.createCredit(userId, accountId, creditLimit, interestRate, dueDate, minimumPayment);
            ctx.status(200).result("Credit created successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void readCredit(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            String creditIdStr = ctx.pathParam("credit_id");

            if (creditIdStr == null) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID creditId = UUID.fromString(creditIdStr);
            AccountCredit credit = AccountCreditsService.readCredit(userId, creditId);
            ctx.json(credit);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void updateCredit(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            String creditIdStr = ctx.pathParam("credit_id");
            String accountIdStr = ctx.formParam("account_id");
            String creditLimitStr = ctx.formParam("credit_limit");
            String interestRateStr = ctx.formParam("interest_rate");
            String dueDateStr = ctx.formParam("due_date");
            String minimumPaymentStr = ctx.formParam("minimum_payment");

            if (creditIdStr == null || accountIdStr == null || creditLimitStr == null || interestRateStr == null || dueDateStr == null || minimumPaymentStr == null) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID creditId = UUID.fromString(creditIdStr);
            UUID accountId = UUID.fromString(accountIdStr);
            long creditLimit = Long.parseLong(creditLimitStr);
            double interestRate = Double.parseDouble(interestRateStr);
            LocalDate dueDate = LocalDate.parse(dueDateStr);
            long minimumPayment = Long.parseLong(minimumPaymentStr);

            AccountCreditsService.updateCredit(userId, creditId, accountId, creditLimit, interestRate, dueDate, minimumPayment);
            ctx.status(200).result("Credit updated successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void deleteCredit(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            String creditIdStr = ctx.pathParam("credit_id");

            if (creditIdStr == null) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID creditId = UUID.fromString(creditIdStr);
            AccountCreditsService.deleteCredit(userId, creditId);
            ctx.status(200).result("Credit deleted successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void calculateInterest(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            String creditIdStr = ctx.pathParam("credit_id");

            if (creditIdStr == null) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID creditId = UUID.fromString(creditIdStr);
            AccountCreditsService.calculateInterest(userId, creditId);
            ctx.status(200).result("Interest calculated and updated successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }
}
