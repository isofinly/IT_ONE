package com.pivo.app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TransactionsController {
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
        try (Connection conn = DatabaseController.connect()) {
            loadTransactions(conn);
            loadCategories(conn);
            populateCategories(conn);

        } catch (SQLException | FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void populateCategories(Connection conn) throws SQLException {
        ObservableList<String> categories = FXCollections.observableArrayList();
        String query = "SELECT name FROM categories";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                categories.add(rs.getString("name"));
            }
        }
        cbCategory.setItems(categories);
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
        transactionsCategoriesList.setItems(categories);
    }

    @FXML
    private void handleAddTransaction() {
        String amountStr = txtAmount.getText();
        LocalDate date = dpTransactionDate.getValue();
        String timeStr = txtTime.getText();
        String category = cbCategory.getValue();
        String description = txtDescription.getText();

        if (amountStr.isEmpty() || date == null || timeStr.isEmpty() || category == null || description.isEmpty()) {
            // TODO log an error message
            System.out.println("All fields must be filled.");
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
        String sql = "INSERT INTO transactions (amount, transaction_date, category_id, description) VALUES (?, ?, (SELECT category_id FROM categories WHERE name = ?), ?)";
        try (Connection conn = DatabaseController.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, amount);
            pstmt.setString(2, dateTimeFormatted);
            pstmt.setString(3, category);
            pstmt.setString(4, description);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                loadTransactions(conn);
            }
        } catch (IOException e) {
            // TODO Handle file not found exception
            throw new RuntimeException(e);
        }
    }
}

