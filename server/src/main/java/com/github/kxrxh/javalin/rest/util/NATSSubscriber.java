package com.github.kxrxh.javalin.rest.util;

import com.github.kxrxh.javalin.rest.database.DatabaseManager;
import com.github.kxrxh.javalin.rest.database.GettingConnectionException;
import com.github.kxrxh.javalin.rest.database.models.Report;
import com.github.kxrxh.javalin.rest.database.models.Transaction;
import com.github.kxrxh.javalin.rest.entities.BudgetAnalysisResult;
import com.github.kxrxh.javalin.rest.entities.CategoryAnalysisResult;
import com.github.kxrxh.javalin.rest.entities.FinancialAdvice;
import com.github.kxrxh.javalin.rest.entities.FinancialForecast;
import com.github.kxrxh.javalin.rest.services.*;
import io.nats.client.Dispatcher;
import io.nats.client.Nats;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class NATSSubscriber {

    private static io.nats.client.Connection natsConnection;

    public static void connect(String natsServerURL, String subject) throws IOException, InterruptedException {
        if (natsConnection == null) {
            natsConnection = Nats.connect(natsServerURL);
            Dispatcher dispatcher = natsConnection.createDispatcher(msg -> {
                String message = new String(msg.getData(), StandardCharsets.UTF_8);
                executePreparedStatement(message);
            });
            dispatcher.subscribe(subject);
        }
    }

    public static void disconnect() throws InterruptedException {
        if (natsConnection != null) {
            natsConnection.close();
        }
    }

    public NATSSubscriber(String natsServerURL, String subject) throws IOException, InterruptedException {

    }

    private static void executePreparedStatement(String message) {
        JSONObject json = new JSONObject(message);
        String sql = json.getString("sql");
        JSONArray paramsJson = json.getJSONArray("params");

        Optional<Connection> opConn = DatabaseManager.getInstance().getConnection();

        if (opConn.isEmpty()) {
            log.error("Could not get connection from database manager", new GettingConnectionException());
            return;
        }

        Connection conn = opConn.get();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setPreparedStatementParams(pstmt, paramsJson);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Error executing prepared statement: {}", e.getMessage());
        }
    }

    private static void setPreparedStatementParams(PreparedStatement pstmt, JSONArray paramsJson) throws SQLException {
        for (int i = 0; i < paramsJson.length(); i++) {
            Object param = paramsJson.get(i);
            if (param instanceof String str) {
                // Check if the string can be parsed as a timestamp
                try {
                    Timestamp ts = Timestamp.valueOf(str);
                    pstmt.setTimestamp(i + 1, ts);
                } catch (IllegalArgumentException e) {
                    // Not a timestamp, set as string
                    pstmt.setString(i + 1, str);
                }
            } else if (param instanceof Integer) {
                pstmt.setInt(i + 1, (Integer) param);
            } else if (param instanceof Double) {
                pstmt.setDouble(i + 1, (Double) param);
            } else {
                pstmt.setObject(i + 1, param);
            }
        }
    }

    private void handleNATSMessage(String message) {
        JSONObject json = new JSONObject(message);
        String action = json.getString("action");

        switch (action) {
            case "transferFunds":
                handleTransferFunds(json);
                break;
            case "mergeAccounts":
                handleMergeAccounts(json);
                break;
            case "searchTransactions":
                handleSearchTransactions(json);
                break;
            case "createRecurringTransaction":
                handleCreateRecurringTransaction(json);
                break;
            case "analyzeCategory":
                handleAnalyzeCategory(json);
                break;
            case "setBudgetAlert":
                handleSetBudgetAlert(json);
                break;
            case "analyzeBudget":
                handleAnalyzeBudget(json);
                break;
            case "generateReport":
                handleGenerateReport(json);
                break;
            case "getReport":
                handleGetReport(json);
                break;
            case "getFinancialAdvice":
                handleGetFinancialAdvice(json);
                break;
            case "getFinancialForecast":
                handleGetFinancialForecast(json);
                break;
            case "integrateWithBank":
                handleIntegrateWithBank(json);
                break;
            case "autoCategorizeTransactions":
                handleAutoCategorizeTransactions(json);
                break;
            case "setNotification":
                handleSetNotification(json);
                break;
            default:
                log.error("Unknown action: " + action);
        }
    }

    private void handleTransferFunds(JSONObject json) {
        try {
            UUID fromAccountId = UUID.fromString(json.getString("from_account_id"));
            UUID toAccountId = UUID.fromString(json.getString("to_account_id"));
            Long amount = json.getLong("amount");
            AccountService.transferFunds(fromAccountId, toAccountId, amount);
        } catch (SQLException e) {
            log.error("Error handling transfer funds: {}", e.getMessage());
        }
    }

    private void handleMergeAccounts(JSONObject json) {
        try {
            JSONArray accountIdsJson = json.getJSONArray("account_ids");
            UUID[] accountIds = new UUID[accountIdsJson.length()];
            for (int i = 0; i < accountIdsJson.length(); i++) {
                accountIds[i] = UUID.fromString(accountIdsJson.getString(i));
            }
            String newAccountName = json.getString("new_account_name");
            String accountType = json.getString("account_type");
            AccountService.mergeAccounts(accountIds, newAccountName, accountType);
        } catch (SQLException e) {
            log.error("Error handling merge accounts: {}", e.getMessage());
        }
    }

    private void handleSearchTransactions(JSONObject json) {
        try {
            UUID userId = UUID.fromString(json.getString("user_id"));
            String amountRange = json.optString("amount_range", null);
            String dateRange = json.optString("date_range", null);
            UUID categoryId = json.has("category_id") ? UUID.fromString(json.getString("category_id")) : null;
            String description = json.optString("description", null);
            List<Transaction> transactions = TransactionService.searchTransactions(userId, amountRange, dateRange,
                    categoryId, description);
            log.info("Search transactions result: {}", transactions);
        } catch (SQLException e) {
            log.error("Error handling search transactions: {}", e.getMessage());
        }
    }

    private void handleCreateRecurringTransaction(JSONObject json) {
        try {
            UUID userId = UUID.fromString(json.getString("user_id"));
            Long amount = json.getLong("amount");
            UUID categoryId = UUID.fromString(json.getString("category_id"));
            String description = json.getString("description");
            String frequency = json.getString("frequency");
            TransactionService.createRecurringTransaction(userId, amount, categoryId, description,
                    Long.valueOf(frequency));
        } catch (SQLException e) {
            log.error("Error handling create recurring transaction: {}", e.getMessage());
        }
    }

    private void handleAnalyzeCategory(JSONObject json) {
        try {
            UUID categoryId = UUID.fromString(json.getString("category_id"));
            String dateRange = json.optString("date_range", null);
            CategoryAnalysisResult result = CategoryService.analyzeCategory(categoryId, dateRange);
            log.info("Analyze category result: {}", result);
        } catch (SQLException e) {
            log.error("Error handling analyze category: {}", e.getMessage());
        }
    }

    private void handleSetBudgetAlert(JSONObject json) {
        try {
            UUID budgetId = UUID.fromString(json.getString("budget_id"));
            Long alertThreshold = json.getLong("alert_threshold");
            BudgetService.setBudgetAlert(budgetId, alertThreshold);
        } catch (SQLException e) {
            log.error("Error handling set budget alert: {}", e.getMessage());
        }
    }

    private void handleAnalyzeBudget(JSONObject json) {
        try {
            UUID budgetId = UUID.fromString(json.getString("budget_id"));
            BudgetAnalysisResult result = BudgetService.analyzeBudget(budgetId);
            log.info("Analyze budget result: {}", result);
        } catch (SQLException e) {
            log.error("Error handling analyze budget: {}", e.getMessage());
        }
    }

    private void handleGenerateReport(JSONObject json) {
        try {
            UUID userId = UUID.fromString(json.getString("user_id"));
            String reportType = json.getString("report_type");
            String dateRange = json.getString("date_range");
            Report result = ReportService.generateReport(userId, reportType, dateRange);
            log.info("Generate report result: {}", result);
        } catch (SQLException e) {
            log.error("Error handling generate report: {}", e.getMessage());
        }
    }

    private void handleGetReport(JSONObject json) {
        try {
            UUID reportId = UUID.fromString(json.getString("report_id"));
            Report result = ReportService.getReport(reportId);
            log.info("Get report result: {}", result);
        } catch (SQLException e) {
            log.error("Error handling get report: {}", e.getMessage());
        }
    }

    private void handleGetFinancialAdvice(JSONObject json) {
        try {
            UUID userId = UUID.fromString(json.getString("user_id"));
            FinancialAdvice result = AdviceService.getFinancialAdvice(userId);
            log.info("Get financial advice result: {}", result);
        } catch (SQLException e) {
            log.error("Error handling get financial advice: {}", e.getMessage());
        }
    }

    private void handleGetFinancialForecast(JSONObject json) {
        try {
            UUID userId = UUID.fromString(json.getString("user_id"));
            String dateRange = json.getString("date_range");
            FinancialForecast result = AdviceService.getFinancialForecast(userId, dateRange);
            log.info("Get financial forecast result: {}", result);
        } catch (SQLException e) {
            log.error("Error handling get financial forecast: {}", e.getMessage());
        }
    }

    private void handleIntegrateWithBank(JSONObject json) {
        try {
            UUID userId = UUID.fromString(json.getString("user_id"));
            String bankCredentials = json.getString("bank_credentials");
            IntegrationService.integrateWithBank(userId, bankCredentials);
        } catch (SQLException e) {
            log.error("Error handling integrate with bank: {}", e.getMessage());
        }
    }

    private void handleAutoCategorizeTransactions(JSONObject json) {
        try {
            UUID userId = UUID.fromString(json.getString("user_id"));
            IntegrationService.autoCategorizeTransactions(userId);
        } catch (SQLException e) {
            log.error("Error handling auto categorize transactions: {}", e.getMessage());
        }
    }

    private void handleSetNotification(JSONObject json) {
        try {
            UUID userId = UUID.fromString(json.getString("user_id"));
            String notificationType = json.getString("notification_type");
            Long threshold = json.getLong("threshold");
            NotificationService.setNotification(userId, notificationType, threshold);
        } catch (SQLException e) {
            log.error("Error handling set notification: {}", e.getMessage());
        }
    }
}
