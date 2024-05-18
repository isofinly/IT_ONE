package com.github.kxrxh.javalin.rest.api;

import static com.github.kxrxh.javalin.rest.util.Prometheus.initializePrometheus;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import com.github.kxrxh.javalin.rest.api.jwt.Utils;
import com.github.kxrxh.javalin.rest.controllers.AccountBalancesController;
import com.github.kxrxh.javalin.rest.controllers.AccountController;
import com.github.kxrxh.javalin.rest.controllers.AccountCreditsController;
import com.github.kxrxh.javalin.rest.controllers.AccountDepositoriesController;
import com.github.kxrxh.javalin.rest.controllers.AccountInvestmentsController;
import com.github.kxrxh.javalin.rest.controllers.AccountLoansController;
import com.github.kxrxh.javalin.rest.controllers.AccountOtherAssetsController;
import com.github.kxrxh.javalin.rest.controllers.AdviceController;
import com.github.kxrxh.javalin.rest.controllers.AuthController;
import com.github.kxrxh.javalin.rest.controllers.BudgetController;
import com.github.kxrxh.javalin.rest.controllers.CategoryController;
import com.github.kxrxh.javalin.rest.controllers.ExchangeRateController;
import com.github.kxrxh.javalin.rest.controllers.IntegrationController;
import com.github.kxrxh.javalin.rest.controllers.RecurringTransactionController;
import com.github.kxrxh.javalin.rest.controllers.ReportController;
import com.github.kxrxh.javalin.rest.controllers.TaxController;
import com.github.kxrxh.javalin.rest.controllers.TransactionController;
import com.github.kxrxh.javalin.rest.controllers.ValuationController;
import com.github.kxrxh.javalin.rest.controllers.VisualizationController;
import com.github.kxrxh.javalin.rest.services.AccountCreditsService;
import com.github.kxrxh.javalin.rest.services.AccountLoansService;
import com.github.kxrxh.javalin.rest.util.PrometheusInitializationException;

import io.javalin.Javalin;
import lombok.extern.slf4j.Slf4j;

/**
 * Represents a RESTful server built with Javalin framework.
 */
@Slf4j
public class RestServer {
    private final Javalin app;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public RestServer(boolean dev) {
        StatisticsHandler statisticsHandler = new StatisticsHandler();
        QueuedThreadPool queuedThreadPool = new QueuedThreadPool(200, 8, 60_000);

        this.app = Javalin.create(config -> {
            if (dev) {
                config.bundledPlugins.enableDevLogging();
            }
            config.jetty.threadPool = queuedThreadPool;
            config.jetty.modifyServer(server -> server.setHandler(statisticsHandler));
            config.bundledPlugins.enableCors(cors -> cors.addRule(it -> it.allowHost("localhost")));
        });
        try {
            initializePrometheus(statisticsHandler, queuedThreadPool);
        } catch (IOException e) {
            log.error("Unable to initialize Prometheus: {}", e.getMessage());
            throw new PrometheusInitializationException(e);
        }
        app.after(ctx -> log.info(ctx.req().getMethod() + " " + ctx.req().getPathInfo() + " " + ctx.statusCode()));

        scheduler.scheduleAtFixedRate(this::checkDueDateNotifications, 0, 1, TimeUnit.DAYS);

    }

    public RestServer() {
        this(false);
    }

    /**
     * Sets up open routes for the RESTful API.
     */
    public void setupRoutes() {
        app.get("/", ctx -> ctx.result("Hello World!"));

        // Account routes
        app.post("/api/v1/accounts/transfer", AccountController::transferFunds);
        app.post("/api/v1/accounts/merge", AccountController::mergeAccounts);

        // new
        app.post("/api/v1/accounts/create_balance", AccountBalancesController::createBalance);
        app.get("/api/v1/accounts/read_balance", AccountBalancesController::readBalance);
        app.put("/api/v1/accounts/update_balance", AccountBalancesController::updateBalance);
        app.delete("/api/v1/accounts/delete_balance", AccountBalancesController::deleteBalance);
        app.get("/api/v1/accounts/calculate_total_balance", AccountBalancesController::calculateTotalBalance);

        app.post("/api/v1/accounts/create_credit", AccountCreditsController::createCredit);
        app.get("/api/v1/accounts/read_credit", AccountCreditsController::readCredit);
        app.put("/api/v1/accounts/update_credit", AccountCreditsController::updateCredit);
        app.delete("/api/v1/accounts/delete_credit", AccountCreditsController::deleteCredit);
        app.get("/api/v1/accounts/update_credit_limit", AccountCreditsController::calculateInterest);

        app.post("/api/v1/accounts/create_depository", AccountDepositoriesController::createDepository);
        app.get("/api/v1/accounts/read_depository", AccountDepositoriesController::readDepository);
        app.put("/api/v1/accounts/update_depository", AccountDepositoriesController::updateDepository);
        app.delete("/api/v1/accounts/delete_depository", AccountDepositoriesController::deleteDepository);
        app.get("/api/v1/accounts/calculate_depository_interest", AccountDepositoriesController::calculateInterest);

        app.post("/api/v1/accounts/create_investment", AccountInvestmentsController::createInvestment);
        app.get("/api/v1/accounts/read_investment", AccountInvestmentsController::readInvestment);
        app.put("/api/v1/accounts/update_investment", AccountInvestmentsController::updateInvestment);
        app.delete("/api/v1/accounts/delete_investment", AccountInvestmentsController::deleteInvestment);
        app.get("/api/v1/accounts/calculate_dividends", AccountInvestmentsController::calculateDividends);

        app.get("/api/v1/accounts/create_loan", AccountLoansController::createLoan);
        app.post("/api/v1/accounts/read_loan", AccountLoansController::readLoan);
        app.put("/api/v1/accounts/update_loan", AccountLoansController::updateLoan);
        app.delete("/api/v1/accounts/delete_loan", AccountLoansController::deleteLoan);
        app.get("/api/v1/accounts/calculate_loan_interest", AccountLoansController::calculateInterest);
        app.get("/api/v1/accounts/check_due_date_notifications", AccountLoansController::checkDueDateNotifications);

        app.get("/api/v1/accounts/create_asset", AccountOtherAssetsController::createAsset);
        app.post("/api/v1/accounts/read_asset", AccountOtherAssetsController::readAsset);
        app.put("/api/v1/accounts/update_asset", AccountOtherAssetsController::updateAsset);
        app.delete("/api/v1/accounts/delete_asset", AccountOtherAssetsController::deleteAsset);
        app.delete("/api/v1/accounts/apply_depreciation", AccountOtherAssetsController::applyDepreciation);

        // Advice routes
        app.get("/api/v1/advice", AdviceController::getFinancialAdvice);

        // new
        app.get("/api/v1/financial_forecast", AdviceController::getFinancialForecast);

        // Budget routes
        app.post("/api/v1/budget/set_budget_alert", BudgetController::setBudgetAlert);
        app.get("/api/v1/budget/analyze_budget", BudgetController::analyzeBudget);

        // Category routes
        app.get("/api/v1/category/analysis", CategoryController::analyzeCategory);
        // new
        app.post("/api/v1/category/create", CategoryController::createCategory);
        app.get("/api/v1/category/read", CategoryController::readCategory);
        app.put("/api/v1/category/update", CategoryController::updateCategory);
        app.delete("/api/v1/category/delete", CategoryController::deleteCategory);

        // new
        app.get("/api/v1/exchange_rate/create", ExchangeRateController::createExchangeRate);
        app.get("/api/v1/exchange_rate/read", ExchangeRateController::readExchangeRate);
        app.put("/api/v1/exchange_rate/update", ExchangeRateController::updateExchangeRate);
        app.delete("/api/v1/exchange_rate/delete", ExchangeRateController::deleteExchangeRate);

        // Integration routes
        app.post("/api/v1/integration/bank", IntegrationController::integrateWithBank);
        app.post("/api/v1/integration/auto-categorize", IntegrationController::autoCategorizeTransactions);

        // Report routes
        app.get("/api/v1/monthly_report", ReportController::generateMonthlyReport);

        // Tax routes
        // new
        app.post("/api/v1/tax/create", TaxController::createTax);
        app.get("/api/v1/tax/read", TaxController::readTax);
        app.put("/api/v1/tax/update", TaxController::updateTax);
        app.delete("/api/v1/tax/delete", TaxController::deleteTax);
        app.get("/api/v1/tax/calculate", TaxController::calculateTaxes);

        // Transaction routes
        // new
        app.post("/api/v1/transaction/recurring/create", RecurringTransactionController::createRecurringTransaction);
        app.get("/api/v1/transaction/recurring/read", RecurringTransactionController::createRecurringTransaction);
        app.put("/api/v1/transaction/recurring/update", RecurringTransactionController::createRecurringTransaction);
        app.delete("/api/v1/transaction/recurring/delete", RecurringTransactionController::createRecurringTransaction);
        app.post("/api/v1/transaction/recurring", TransactionController::createRecurringTransaction);
        app.get("/api/v1/transaction/search", TransactionController::searchTransactions);

        app.post("/api/v1/transaction/create", TransactionController::createTransaction);
        app.put("/api/v1/transaction/update", TransactionController::updateTransaction);
        app.delete("/api/v1/transaction/delete", TransactionController::deleteTransaction);

        // Valuation routes
        // new
        app.post("/api/v1/valuation/create", ValuationController::createValuation);
        app.get("/api/v1/valuation/read", ValuationController::readValuation);
        app.put("/api/v1/valuation/update", ValuationController::updateValuation);
        app.delete("/api/v1/valuation/delete", ValuationController::deleteValuation);

        // new
        app.post("/api/v1/bank/integration", IntegrationController::integrateWithBank);

        // TODO: Visualization routes
        app.get("/api/v1/visualizations", VisualizationController::getVisualization);
    }

    /**
     * Sets up JWT authentication for protected routes.
     *
     * @param protectedRoute The route prefix for protected routes.
     * @param jwtSecret      The secret key used for JWT token generation and
     *                       validation.
     */
    public void setupJWTAuthentication(String protectedRoute, String jwtSecret) {
        AuthController.initialize("my-key");
        app.before(protectedRoute + "/*", AuthController::securityRouteHandler);
        app.post(protectedRoute + "/login", AuthController::loginRouteHandler);
        app.post(protectedRoute + "/register", AuthController::registerRouteHandler);
        app.before(AuthController.getInstance().getDecodeHandler());
    }

    /**
     * Starts the server and listens on the specified port.
     *
     * @param port The port number on which the server will listen.
     */
    public void listen(int port) {
        app.start(port);
    }

    private void checkDueDateNotifications() {
        for (UUID userId : Utils.getAllUserIds()) {
            try {
                AccountLoansService.checkDueDateNotifications(userId);
                AccountCreditsService.checkDueDateNotifications(userId);
            } catch (SQLException e) {
                log.error("Error checking due date notifications for user {}: {}", userId, e.getMessage());
            }
        }
    }

}