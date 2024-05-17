package com.pivo.app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.util.StringConverter;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static com.pivo.app.Application.conn;
import static com.pivo.app.Application.showAlert;
import static com.pivo.app.ConfigManager.getConfig;


public class DashboardController {
    @FXML
    private LineChart<Number, Number> netWorthChart;
    @FXML
    private Label netWorthValue;
    @FXML
    private PieChart assetsDebtsChart;
    @FXML
    private Label incomeValue;
    @FXML
    private LineChart<String, Number> incomeChart;
    @FXML
    private Label spendingValue;
    @FXML
    private LineChart<String, Number> spendingChart;
    @FXML
    private Label savingsRateValue;
    @FXML
    private LineChart<String, Number> savingsRateChart;
    @FXML
    private Label investingValue;
    @FXML
    private LineChart<String, Number> investingChart;
    @FXML
    private ListView<String> transactionsList;
    @FXML
    private ListView<String> dashboardCategoriesList;

    private static final String USER_ID_QUERY = "(SELECT user_id FROM users WHERE username = ?)";
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String MONTH_FORMAT = "%Y-%m";
    private static final String SELECTED_USER = "selectedUser";
    private static final int MAX_TRANSACTIONS = 10;
    private static final String TOTAL = "total";

    @FXML
    private void initialize() {
        try {
            updateNetWorthChart(conn);
            updateAssetsDebtsChart(conn);
            updateFinancialCharts(conn);
            loadTransactions(conn);
            loadCategories(conn);
        } catch (SQLException e) {
            showAlert("Error", "Failed to fetch data", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void updateFinancialCharts(Connection conn) throws SQLException {
        updateChartForCategory(conn, incomeChart, incomeValue, 1); // ID for Income
        updateChartForCategory(conn, spendingChart, spendingValue, 2); // ID for Spending
        updateChartForCategory(conn, savingsRateChart, savingsRateValue, 3); // ID for Savings
        updateChartForCategory(conn, investingChart, investingValue, 4); // ID for Investing
    }

    private void configureXAxis(NumberAxis xAxis) {
        xAxis.setTickLabelFormatter(new StringConverter<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT).withZone(ZoneId.systemDefault());

            @Override
            public String toString(Number object) {
                return object == null ? "" : formatter.format(Instant.ofEpochMilli(object.longValue()));
            }

            @Override
            public Number fromString(String string) {
                return Instant.from(formatter.parse(string)).toEpochMilli();
            }
        });
    }

    private void updateAssetsDebtsChart(Connection conn) throws SQLException {
        double assets = 0;
        double debts = 0;
        String selectedUser = getConfig(SELECTED_USER);
        String query = "SELECT account_type, SUM(balance) as total FROM accounts WHERE user_id = " + USER_ID_QUERY + " GROUP BY account_type";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, selectedUser);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    if ("Asset".equals(rs.getString("account_type"))) {
                        assets += rs.getDouble(TOTAL);
                    } else if ("Debt".equals(rs.getString("account_type"))) {
                        debts += rs.getDouble(TOTAL);
                    }
                }
            }
        }

        PieChart.Data slice1 = new PieChart.Data("Assets", assets);
        PieChart.Data slice2 = new PieChart.Data("Debts", debts);
        assetsDebtsChart.getData().addAll(slice1, slice2);
    }

    private void updateNetWorthChart(Connection conn) throws SQLException {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Net Worth Over Time");

        String rangeQuery = "SELECT MIN(transaction_date) AS minDate, MAX(transaction_date) AS maxDate FROM transactions";
        LocalDateTime minDate = LocalDateTime.now();
        LocalDateTime maxDate = LocalDateTime.now();
        try (Statement rangeStmt = conn.createStatement(); ResultSet rsRange = rangeStmt.executeQuery(rangeQuery)) {
            if (rsRange.next()) {
                minDate = LocalDateTime.parse(rsRange.getString("minDate"), DateTimeFormatter.ofPattern(DATE_FORMAT));
                maxDate = LocalDateTime.parse(rsRange.getString("maxDate"), DateTimeFormatter.ofPattern(DATE_FORMAT));
            }
        }

        long minEpochMilli = minDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long maxEpochMilli = maxDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        NumberAxis xAxis = (NumberAxis) netWorthChart.getXAxis();
        configureXAxis(xAxis);
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(minEpochMilli);
        xAxis.setUpperBound(maxEpochMilli);
        xAxis.setTickUnit((double) (maxEpochMilli - minEpochMilli) / 10);

        String selectedUser = getConfig(SELECTED_USER);
        String query = "SELECT transaction_date, SUM(amount) OVER (ORDER BY transaction_date) as cumulative_sum, u.user_id FROM transactions t JOIN users u ON t.user_id = u.user_id WHERE u.user_id = " + USER_ID_QUERY + " ORDER BY transaction_date";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, selectedUser);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LocalDateTime dateTime = LocalDateTime.parse(rs.getString("transaction_date"), DateTimeFormatter.ofPattern(DATE_FORMAT));
                    long epochMilli = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                    double cumulativeSum = rs.getDouble("cumulative_sum");
                    XYChart.Data<Number, Number> dataPoint = new XYChart.Data<>(epochMilli, cumulativeSum / 100.0);
                    series.getData().add(dataPoint);
                    Tooltip tooltip = new Tooltip(dateTime.format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
                    Tooltip.install(dataPoint.getNode(), tooltip);
                }
            }
        }
        netWorthValue.setText(String.format("%.2f", series.getData().get(series.getData().size() - 1).getYValue()));
        netWorthChart.getData().add(series);
        netWorthChart.setCreateSymbols(true);
    }

    private void updateChartForCategory(Connection conn, LineChart<String, Number> chart, Label valueLabel, int categoryId) throws SQLException {
        String selectedUser = getConfig(SELECTED_USER);
        String query = "SELECT strftime('" + MONTH_FORMAT + "', t.transaction_date) AS month, SUM(t.amount) AS total FROM transactions t JOIN users u ON t.user_id = u.user_id WHERE t.category_id = ? AND u.user_id = " + USER_ID_QUERY + " GROUP BY month ORDER BY month";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, categoryId);
            stmt.setString(2, selectedUser);
            try (ResultSet rs = stmt.executeQuery()) {
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                while (rs.next()) {
                    String month = rs.getString("month");
                    double total = rs.getDouble(TOTAL) / 100.0;
                    series.getData().add(new XYChart.Data<>(month, total));
                }
                series.setName(fetchCategoryName(conn, categoryId));
                chart.getData().clear();
                chart.getData().add(series);
                if (!series.getData().isEmpty()) {
                    XYChart.Data<String, Number> lastData = series.getData().get(series.getData().size() - 1);
                    valueLabel.setText(String.format("%,.2f", lastData.getYValue()));
                }
            }
        }
    }

    private String fetchCategoryName(Connection conn, int categoryId) throws SQLException {
        String selectedUser = getConfig(SELECTED_USER);
        String query = "SELECT c.name FROM categories c JOIN transactions t ON c.category_id = t.category_id JOIN users u ON t.user_id = u.user_id WHERE c.category_id = ? AND u.user_id = " + USER_ID_QUERY + " LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, categoryId);
            stmt.setString(2, selectedUser);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
                // TODO handle error
                return "Unknown Category";
            }
        }
    }

    private void loadTransactions(Connection conn) throws SQLException {
        String selectedUser = getConfig(SELECTED_USER);
        ObservableList<String> transactions = FXCollections.observableArrayList();
        String query = "SELECT t.transaction_date, t.description, t.amount FROM transactions t JOIN users u ON t.user_id = u.user_id WHERE u.user_id = " + USER_ID_QUERY + " ORDER BY t.transaction_date DESC LIMIT " + MAX_TRANSACTIONS;
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, selectedUser);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String entry = String.format("%s - %s: %.2f",
                            rs.getString("transaction_date"),
                            rs.getString("description"),
                            rs.getDouble("amount") / 100.0);
                    transactions.add(entry);
                }
            }
        }
        transactionsList.setItems(transactions);
    }

    private void loadCategories(Connection conn) throws SQLException {
        String selectedUser = getConfig(SELECTED_USER);
        ObservableList<String> categories = FXCollections.observableArrayList();
        String query = "SELECT c.name, SUM(t.amount) as total FROM transactions t JOIN categories c ON t.category_id = c.category_id JOIN users u ON t.user_id = u.user_id WHERE u.user_id = " + USER_ID_QUERY + " GROUP BY c.category_id";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, selectedUser);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String entry = String.format("%s: %.2f",
                            rs.getString("name"),
                            rs.getDouble(TOTAL) / 100.0);
                    categories.add(entry);
                }
            }
        }
        dashboardCategoriesList.setItems(categories);
    }
}
