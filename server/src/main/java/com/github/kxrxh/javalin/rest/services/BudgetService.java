package com.github.kxrxh.javalin.rest.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.github.kxrxh.javalin.rest.database.ConnectionRetrievingException;
import com.github.kxrxh.javalin.rest.database.DatabaseManager;
import com.github.kxrxh.javalin.rest.database.models.Transaction;
import com.github.kxrxh.javalin.rest.database.models.Transaction.TransactionType;
import com.github.kxrxh.javalin.rest.entities.BudgetAnalysisResult;
import com.github.kxrxh.javalin.rest.entities.BudgetComparisonResult;
import com.github.kxrxh.javalin.rest.entities.BudgetSuggestions;

// TODO @KXRXH: Check logic
// TODO: Логика сравнения бюджетов за прошлые периоды и возможные предложения по лимитам и интервалам
public class BudgetService extends AbstractService {

    private BudgetService() {
    }

    public static void setBudgetAlert(UUID budgetId, long alertThreshold) throws SQLException {
        Optional<Connection> opConn = DatabaseManager.getInstance().getConnection();

        if (opConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = opConn.get();

        String query = "UPDATE budgets SET alert_threshold = ? WHERE budget_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setLong(1, alertThreshold);
            ps.setObject(2, budgetId, java.sql.Types.OTHER);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Could not set budget alert threshold", e);
        }
    }

    public static BudgetAnalysisResult analyzeBudget(UUID userId, UUID budgetId, String dateRange) throws SQLException {
        BudgetAnalysisResult result = new BudgetAnalysisResult();
        List<Transaction> transactions = new ArrayList<>();

        Optional<Connection> opConn = DatabaseManager.getInstance().getConnection();
        if (opConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = opConn.get();

        // Fetch the family ID for the given user
        UUID familyId = getFamilyIdForUser(conn, userId);
        if (familyId == null) {
            throw new SQLException("No family found for the given user.");
        }

        // Retrieve the budget for the user
        String budgetQuery = "SELECT * FROM budgets WHERE budget_id = ? AND (user_id = ? OR user_id IS NULL)";
        try (PreparedStatement ps = conn.prepareStatement(budgetQuery)) {
            ps.setObject(1, budgetId, java.sql.Types.OTHER);
            ps.setObject(2, userId, java.sql.Types.OTHER);

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new SQLException("Budget not found for the user.");
            }

            result.setBudgetId(UUID.fromString(rs.getString("budget_id")));
            result.setUserId(UUID.fromString(rs.getString("user_id")));
            result.setCategoryId(UUID.fromString(rs.getString("category_id")));
            result.setLimitAmount(rs.getLong("limit_amount"));
            result.setStartDate(rs.getTimestamp("start_date").toLocalDateTime());
            result.setEndDate(
                    rs.getTimestamp("end_date") != null ? rs.getTimestamp("end_date").toLocalDateTime() : null);
            result.setAlertThreshold(rs.getLong("alert_threshold"));
        }

        // Retrieve transactions for the budget within the date range
        String transactionQuery = "SELECT * FROM transactions WHERE category_id = ? AND date BETWEEN ? AND ?";
        List<Object> params = new ArrayList<>();
        params.add(result.getCategoryId());
        String[] dates = dateRange.split("to");
        params.add(Timestamp.valueOf(dates[0].trim()));
        params.add(Timestamp.valueOf(dates[1].trim()));

        try (PreparedStatement ps = conn.prepareStatement(transactionQuery)) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            long totalSpent = 0;
            while (rs.next()) {
                Transaction transaction = Transaction.builder()
                        .transactionId(UUID.fromString(rs.getString("transaction_id")))
                        .name(rs.getString("name"))
                        .date(rs.getTimestamp("date").toLocalDateTime())
                        .amount(rs.getLong("amount"))
                        .currency(rs.getString("currency"))
                        .accountId(UUID.fromString(rs.getString("account_id")))
                        .categoryId(rs.getString("category_id") != null ? UUID.fromString(rs.getString("category_id"))
                                : null)
                        .excluded(rs.getBoolean("excluded"))
                        .notes(rs.getString("notes"))
                        .transactionType(TransactionType.valueOf(rs.getString("transaction_type").toUpperCase()))
                        .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                        .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                        .lastSyncedAt(rs.getTimestamp("last_synced_at") != null
                                ? rs.getTimestamp("last_synced_at").toLocalDateTime()
                                : null)
                        .build();
                transactions.add(transaction);
                totalSpent += transaction.getAmount();
            }

            result.setTransactions(transactions);
            result.setTotalSpent(totalSpent);
        }

        // Compare with past periods and provide suggestions
        List<BudgetComparisonResult> comparisons = compareWithPastPeriods(conn, result.getCategoryId(), dateRange);
        result.setComparisons(comparisons);

        // Suggest new budget limits and intervals
        BudgetSuggestions suggestions = suggestNewBudgetLimits(result.getTotalSpent(), result.getLimitAmount());
        result.setSuggestions(suggestions);

        return result;
    }

    private static UUID getFamilyIdForUser(Connection conn, UUID userId) throws SQLException {
        String query = "SELECT family_id FROM users WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setObject(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return UUID.fromString(rs.getString("family_id"));
                } else {
                    return null;
                }
            }
        }
    }

    private static List<BudgetComparisonResult> compareWithPastPeriods(Connection conn, UUID categoryId,
            String dateRange) throws SQLException {
        List<BudgetComparisonResult> comparisons = new ArrayList<>();

        String pastPeriodsQuery = "SELECT * FROM transactions WHERE category_id = ? AND date < ?";
        String[] dates = dateRange.split("to");
        String endDate = dates[1].trim();

        try (PreparedStatement ps = conn.prepareStatement(pastPeriodsQuery)) {
            ps.setObject(1, categoryId);
            ps.setTimestamp(2, Timestamp.valueOf(endDate));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                BudgetComparisonResult comparison = new BudgetComparisonResult();
                comparison.setTransactionId(UUID.fromString(rs.getString("transaction_id")));
                comparison.setName(rs.getString("name"));
                comparison.setDate(rs.getTimestamp("date").toLocalDateTime());
                comparison.setAmount(rs.getLong("amount"));
                comparison.setCurrency(rs.getString("currency"));
                comparison.setAccountId(UUID.fromString(rs.getString("account_id")));
                comparison.setCategoryId(
                        rs.getString("category_id") != null ? UUID.fromString(rs.getString("category_id")) : null);
                comparison.setExcluded(rs.getBoolean("excluded"));
                comparison.setNotes(rs.getString("notes"));
                comparison.setTransactionType(TransactionType.valueOf(rs.getString("transaction_type").toUpperCase()));
                comparison.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                comparison.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                comparison.setLastSyncedAt(
                        rs.getTimestamp("last_synced_at") != null ? rs.getTimestamp("last_synced_at").toLocalDateTime()
                                : null);

                comparisons.add(comparison);
            }
        }
        return comparisons;
    }

    private static BudgetSuggestions suggestNewBudgetLimits(long totalSpent, long limitAmount) {
        BudgetSuggestions suggestions = new BudgetSuggestions();
        suggestions.setCurrentLimit(limitAmount);

        if (totalSpent > limitAmount) {
            suggestions.setSuggestedLimit((long) (totalSpent * 1.1)); // Increase limit by 10% if overspent
        } else {
            suggestions.setSuggestedLimit((long) (totalSpent * 0.9)); // Decrease limit by 10% if underspent
        }

        suggestions.setSuggestedInterval("Monthly"); // Example suggestion, can be more sophisticated

        return suggestions;
    }
}
