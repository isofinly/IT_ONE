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
import com.github.kxrxh.javalin.rest.util.NATSUtil;

public class BudgetService extends AbstractService {

    private BudgetService() {
    }

    /**
     * Sets the alert threshold for a specific budget.
     *
     * @param budgetId       The ID of the budget to set the alert for.
     * @param alertThreshold The threshold value for the alert.
     * @throws SQLException If an SQL error occurs during the operation.
     */
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
        } finally {
            conn.close();
        }
    }

    /**
     * Analyzes a budget for a specific user within a given date range.
     *
     * @param userId    The ID of the user whose budget is being analyzed.
     * @param budgetId  The ID of the budget to analyze.
     * @param dateRange The date range for the analysis.
     * @return The analysis result containing budget details, transactions,
     * comparisons, and suggestions.
     * @throws SQLException If an SQL error occurs during the analysis.
     */
    public static BudgetAnalysisResult analyzeBudget(UUID userId, UUID budgetId, String dateRange) throws SQLException {
        BudgetAnalysisResult result = new BudgetAnalysisResult();
        List<Transaction> transactions = new ArrayList<>();

        Optional<Connection> opConn = DatabaseManager.getInstance().getConnection();
        if (opConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = opConn.get();

        UUID familyId = getFamilyIdForUser(conn, userId);
        if (familyId == null) {
            throw new SQLException("No family found for the given user.");
        }

        String budgetQuery = "SELECT budget_id, user_id, category_id, limit_amount, start_date, end_date, alert_threshold FROM budgets WHERE budget_id = ? AND (user_id = ? OR user_id IS NULL)";
        try (PreparedStatement ps = conn.prepareStatement(budgetQuery)) {
            ps.setObject(1, budgetId, java.sql.Types.OTHER);
            ps.setObject(2, userId, java.sql.Types.OTHER);

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new SQLException("Budget not found for the user.");
            }

            result.setBudgetId(UUID.fromString(rs.getString(BUDGET_ID)));
            result.setUserId(UUID.fromString(rs.getString(USER_ID)));
            result.setCategoryId(UUID.fromString(rs.getString(CATEGORY_ID)));
            result.setLimitAmount(rs.getLong("limit_amount"));
            result.setStartDate(rs.getTimestamp("start_date").toLocalDateTime());
            result.setEndDate(
                    rs.getTimestamp("end_date") != null ? rs.getTimestamp("end_date").toLocalDateTime() : null);
            result.setAlertThreshold(rs.getLong("alert_threshold"));
        }

        String transactionQuery = "SELECT transaction_id, name, date, amount, currency, account_id, category_id, excluded, notes, transaction_type, created_at, updated_at, last_synced_at FROM transactions WHERE category_id = ? AND date BETWEEN ? AND ?";
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
                        .transactionId(UUID.fromString(rs.getString(TRANSACTION_ID)))
                        .name(rs.getString(NAME))
                        .date(rs.getTimestamp(DATE).toLocalDateTime())
                        .amount(rs.getLong(AMOUNT))
                        .currency(rs.getString(CURRENCY))
                        .accountId(UUID.fromString(rs.getString(ACCOUNT_ID)))
                        .categoryId(rs.getString(CATEGORY_ID) != null ? UUID.fromString(rs.getString(CATEGORY_ID))
                                : null)
                        .excluded(rs.getBoolean("excluded"))
                        .notes(rs.getString("notes"))
                        .transactionType(TransactionType.valueOf(rs.getString("transaction_type").toUpperCase()))
                        .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                        .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                        .lastSyncedAt(rs.getTimestamp(LAST_SYNCED_AT) != null
                                ? rs.getTimestamp(LAST_SYNCED_AT).toLocalDateTime()
                                : null)
                        .build();
                transactions.add(transaction);
                totalSpent += transaction.getAmount();
            }

            result.setTransactions(transactions);
            result.setTotalSpent(totalSpent);

            checkBudgetNotifications(userId, result);

        } finally {
            conn.close();
        }

        List<BudgetComparisonResult> comparisons = compareWithPastPeriods(result.getCategoryId(), dateRange);
        result.setComparisons(comparisons);

        BudgetSuggestions suggestions = suggestNewBudgetLimits(result.getTotalSpent(), result.getLimitAmount());
        result.setSuggestions(suggestions);

        return result;
    }

    /**
     * Retrieves the family ID associated with the specified user.
     *
     * @param conn   The database connection.
     * @param userId The ID of the user.
     * @return The family ID associated with the user, or null if not found.
     * @throws SQLException If an SQL error occurs during the retrieval process.
     */
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

    /**
     * Compares transactions of a specific category with transactions from past
     * periods.
     *
     * @param conn       The database connection.
     * @param categoryId The ID of the category.
     * @param dateRange  The date range for the analysis.
     * @return A list of comparison results between current and past transactions.
     * @throws SQLException If an SQL error occurs during the comparison.
     */
    private static List<BudgetComparisonResult> compareWithPastPeriods(UUID categoryId,
            String dateRange) throws SQLException {

        Optional<Connection> opConn = DatabaseManager.getInstance().getConnection();
        if (opConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = opConn.get();

        List<BudgetComparisonResult> comparisons = new ArrayList<>();

        String pastPeriodsQuery = "SELECT transaction_id, name, date, amount, currency, account_id, category_id, excluded, notes, transaction_type, created_at, updated_at, last_synced_at FROM transactions WHERE category_id = ? AND date < ?";
        String[] dates = dateRange.split("to");
        String endDate = dates[1].trim();

        try (PreparedStatement ps = conn.prepareStatement(pastPeriodsQuery)) {
            ps.setObject(1, categoryId);
            ps.setTimestamp(2, Timestamp.valueOf(endDate));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                BudgetComparisonResult comparison = new BudgetComparisonResult();
                comparison.setTransactionId(UUID.fromString(rs.getString(TRANSACTION_ID)));
                comparison.setName(rs.getString(NAME));
                comparison.setDate(rs.getTimestamp(DATE).toLocalDateTime());
                comparison.setAmount(rs.getLong(AMOUNT));
                comparison.setCurrency(rs.getString(CURRENCY));
                comparison.setAccountId(UUID.fromString(rs.getString(ACCOUNT_ID)));
                comparison.setCategoryId(
                        rs.getString(CATEGORY_ID) != null ? UUID.fromString(rs.getString(CATEGORY_ID)) : null);
                comparison.setExcluded(rs.getBoolean("excluded"));
                comparison.setNotes(rs.getString("notes"));
                comparison.setTransactionType(TransactionType.valueOf(rs.getString("transaction_type").toUpperCase()));
                comparison.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                comparison.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                comparison.setLastSyncedAt(
                        rs.getTimestamp(LAST_SYNCED_AT) != null ? rs.getTimestamp(LAST_SYNCED_AT).toLocalDateTime()
                                : null);

                comparisons.add(comparison);
            }
        } finally {
            conn.close();
        }

        return comparisons;
    }

    /**
     * Suggests new budget limits based on total spent amount and current limit.
     *
     * @param totalSpent  The total amount spent within the budget.
     * @param limitAmount The current budget limit.
     * @return Budget suggestions including a suggested limit and interval.
     */
    private static BudgetSuggestions suggestNewBudgetLimits(long totalSpent, long limitAmount) {
        BudgetSuggestions suggestions = new BudgetSuggestions();
        suggestions.setCurrentLimit(limitAmount);

        if (totalSpent > limitAmount) {
            suggestions.setSuggestedLimit((long) (totalSpent * 1.1));
        } else {
            suggestions.setSuggestedLimit((long) (totalSpent * 0.9));
        }

        suggestions.setSuggestedInterval("Monthly");

        return suggestions;
    }

    /**
     * Checks budget notifications based on total spent amount and budget limit.
     *
     * @param userId The ID of the user.
     * @param result The budget analysis result containing budget details.
     */
    private static void checkBudgetNotifications(UUID userId, BudgetAnalysisResult result) {
        long totalSpent = result.getTotalSpent();
        long limitAmount = result.getLimitAmount();
        long threshold10 = (long) (limitAmount * 0.1);
        long threshold5 = (long) (limitAmount * 0.05);

        if (totalSpent >= limitAmount - threshold10) {
            NATSUtil.publish(userId.toString(), "You are within 10% of your budget limit.");
        }
        if (totalSpent >= limitAmount - threshold5) {
            NATSUtil.publish(userId.toString(), "You are within 5% of your budget limit.");
        }
        if (totalSpent >= limitAmount) {
            NATSUtil.publish(userId.toString(), "You have reached your budget limit.");
        }
    }
}
