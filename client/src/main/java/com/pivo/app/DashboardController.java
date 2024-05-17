package com.pivo.app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.util.StringConverter;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;


public class DashboardController {
    @FXML
    private LineChart<Number, Number> netWorthChart;
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

    private Connection connect() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:src/main/resources/com/pivo/app/data.db");
    }

    // TODO: Show values as float from int
    @FXML
    private void initialize() {
        try (Connection conn = connect()) {
            updateNetWorthChart(conn);
            updateAssetsDebtsChart(conn);
            updateFinancialCharts(conn);
            loadTransactions(conn);
            loadCategories(conn);
        } catch (SQLException e) {
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
        // Set up the axis with a custom tick label formatter
        xAxis.setTickLabelFormatter(new StringConverter<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

            @Override
            public String toString(Number object) {
                if (object == null) {
                    return "";
                }
                return formatter.format(Instant.ofEpochMilli(object.longValue()));
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
        String query = "SELECT account_type, SUM(balance) as total FROM accounts GROUP BY account_type";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                if (rs.getString("account_type").equals("Asset")) {
                    assets += rs.getDouble("total");
                } else if (rs.getString("account_type").equals("Debt")) {
                    debts += rs.getDouble("total");
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

        // Fetch the range for the X-axis
        String rangeQuery = "SELECT MIN(transaction_date) AS minDate, MAX(transaction_date) AS maxDate FROM transactions";
        LocalDateTime minDate = LocalDateTime.now();
        LocalDateTime maxDate = LocalDateTime.now();
        try (Statement rangeStmt = conn.createStatement(); ResultSet rsRange = rangeStmt.executeQuery(rangeQuery)) {
            if (rsRange.next()) {
                minDate = LocalDateTime.parse(rsRange.getString("minDate"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                maxDate = LocalDateTime.parse(rsRange.getString("maxDate"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
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

        String query = "SELECT transaction_date, SUM(amount) OVER (ORDER BY transaction_date) as cumulative_sum FROM transactions ORDER BY transaction_date";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String dateTimeStr = rs.getString("transaction_date");
                LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                long epochMilli = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                double cumulativeSum = rs.getDouble("cumulative_sum");

                XYChart.Data<Number, Number> dataPoint = new XYChart.Data<>(epochMilli, cumulativeSum / 100.0);
                series.getData().add(dataPoint);
                Tooltip tooltip = new Tooltip(dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                Tooltip.install(dataPoint.getNode(), tooltip);
            }
        }

        netWorthChart.getData().add(series);
        netWorthChart.setCreateSymbols(true);
    }

    private void updateChartForCategory(Connection conn, LineChart<String, Number> chart, Label valueLabel, int categoryId) throws SQLException {
        String query = "SELECT strftime('%Y-%m', transaction_date) AS month, SUM(amount) AS total " +
                "FROM transactions WHERE category_id = ? " +
                "GROUP BY month ORDER BY month";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, categoryId);
        ResultSet rs = stmt.executeQuery();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        while (rs.next()) {
            String month = rs.getString("month");
            double total = rs.getDouble("total") / 100.0; // Convert cents to dollar
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

    private String fetchCategoryName(Connection conn, int categoryId) throws SQLException {
        String query = "SELECT name FROM categories WHERE category_id = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, categoryId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getString("name");
        }
        // TODO handle error
        return "Unknown Category";
    }

    private void loadTransactions(Connection conn) throws SQLException {
        ObservableList<String> transactions = FXCollections.observableArrayList();
        String query = "SELECT transaction_date, description, amount FROM transactions ORDER BY transaction_date DESC LIMIT 10";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String entry = String.format("%s - %s: %.2f",
                        rs.getString("transaction_date"),
                        rs.getString("description"),
                        rs.getDouble("amount") / 100.0); // Convert cents to dollars
                transactions.add(entry);
            }
        }
        transactionsList.setItems(transactions);
    }

    private void loadCategories(Connection conn) throws SQLException {
        ObservableList<String> categories = FXCollections.observableArrayList();
        String query = "SELECT name, SUM(amount) as total FROM transactions JOIN categories ON transactions.category_id = categories.category_id GROUP BY categories.category_id";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String entry = String.format("%s: %.2f",
                        rs.getString("name"),
                        rs.getDouble("total") / 100.0); // Convert cents to dollars
                categories.add(entry);
            }
        }
        dashboardCategoriesList.setItems(categories);
    }
}
