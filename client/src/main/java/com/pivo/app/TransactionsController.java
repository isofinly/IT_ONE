package com.pivo.app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static com.pivo.app.Application.selectedUser;
import static com.pivo.app.Application.showAlert;

public class TransactionsController {
    private static final String USER_ID_QUERY = "(SELECT user_id FROM users WHERE username = ?)";
    private static final String TOTAL = "total";

    @FXML
    private TextField txtAmount;
    @FXML
    private DatePicker dpTransactionDate;
    @FXML
    private TextField txtTime;
    @FXML
    private ComboBox<String> cbCategory;
    @FXML
    private TextField txtDescription;
    @FXML
    private TableView<String> transactionsTable;
    @FXML
    private TableView<String> dashboardCategoriesList;
    @FXML
    private ListView<String> transactionsList;
    @FXML
    private ListView<String> transactionsCategoriesList;

    public void initialize() {
        try {
            loadTransactions();
            loadCategories();
            populateCategories();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private void populateCategories() throws SQLException {
        ObservableList<String> categories = FXCollections.observableArrayList();
        String query = "SELECT DISTINCT name FROM categories WHERE user_id = (SELECT user_id FROM users WHERE username = ? )";
        try (Connection conn = DatabaseController.connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, selectedUser);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                categories.add(rs.getString("name"));
            }
        }
        cbCategory.setItems(categories);
    }

    private void loadTransactions() throws SQLException {
        ObservableList<String> transactions = FXCollections.observableArrayList();
        String query = "SELECT t.transaction_date, t.description, t.amount FROM transactions t JOIN users u ON t.user_id = u.user_id WHERE u.user_id = " + USER_ID_QUERY + " ORDER BY t.transaction_date DESC";
        try (Connection conn = DatabaseController.connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
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

    private void loadCategories() throws SQLException {
        ObservableList<String> categories = FXCollections.observableArrayList();
        String query = "SELECT c.name, SUM(t.amount) as total FROM transactions t JOIN categories c ON t.category_id = c.category_id JOIN users u ON t.user_id = u.user_id WHERE u.user_id = " + USER_ID_QUERY + " GROUP BY c.category_id";
        try (Connection conn = DatabaseController.connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
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
        transactionsCategoriesList.setItems(categories);
    }

    @FXML
    private void handleAddTransaction() {
        String amountStr = txtAmount.getText();
        LocalDate date = dpTransactionDate.getValue();
        String timeStr = txtTime.getText();
        String category = cbCategory.getValue();
        String description = txtDescription.getText();

        if (amountStr.isEmpty() || date == null || timeStr.isEmpty() || category == null) {
            // TODO log an error message
            System.out.println("All necessary fields must be filled.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr) * 100;  // Convert to cents
            LocalTime time = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm:ss"));
            LocalDateTime dateTime = LocalDateTime.of(date, time);
            insertTransaction(dateTime, amount, category, description);
        } catch (DateTimeParseException | NumberFormatException e) {
            // TODO Handle invalid input formatting
            System.out.println("Invalid input: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            // TODO Handle SQL exceptions here
        }
    }

    private void insertTransaction(LocalDateTime dateTime, double amount, String category, String description) throws SQLException {
        String dateTimeFormatted = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String sql = "INSERT INTO transactions (user_id, amount, transaction_date, category_id, description) VALUES (?, ?, ?, (SELECT category_id FROM categories WHERE name = ?), ?)";
        try (Connection conn = DatabaseController.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, Application.fetchUserId());  // Set user_id
            pstmt.setDouble(2, amount);
            pstmt.setString(3, dateTimeFormatted);
            pstmt.setString(4, category);
            pstmt.setString(5, description);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                loadTransactions();  // Assuming there is a method to refresh the transactions view
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to insert transaction: " + e.getMessage(), Alert.AlertType.ERROR);
            throw new RuntimeException(e);
        }
    }

}

