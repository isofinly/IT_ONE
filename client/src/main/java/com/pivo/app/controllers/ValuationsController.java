package com.pivo.app.controllers;

import static com.pivo.app.App.publisher;
import static com.pivo.app.App.showAlert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;

import com.pivo.app.entities.Account;
import com.pivo.app.util.ConfigManager;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValuationsController {
    private static final String ERROR = "Error";
    @FXML
    private TableView<Account> assetsTable;
    @FXML
    private ListView<String> categoryList;
    @FXML
    private TextField newCategoryName;
    private Integer userId;
    @FXML
    private TableColumn<Account, String> accountNameColumn;
    @FXML
    private TableColumn<Account, String> accountTypeColumn;
    @FXML
    private TableColumn<Account, Number> balanceColumn;

    public void initialize() {
        String userName = ConfigManager.getConfig("selectedUser");
        fetchUserId(userName);

        accountNameColumn.setCellValueFactory(cellData -> cellData.getValue().accountNameProperty());
        accountTypeColumn.setCellValueFactory(cellData -> cellData.getValue().accountTypeProperty());
        balanceColumn.setCellValueFactory(cellData -> cellData.getValue().balanceProperty());

        loadAssets();
        loadCategories();
    }

    private void loadAssets() {
        String sql = "SELECT account_name, account_type, balance FROM accounts WHERE user_id = ? AND account_type = 'Asset'";
        try (Connection conn = DatabaseManager.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                assetsTable.getItems().add(new Account(rs.getString("account_name"), rs.getString("account_type"),
                        rs.getDouble("balance")));
            }
        } catch (SQLException e) {
            log.error("Failed to fetch assets", e);
            showAlert(ERROR, "Failed to fetch assets", Alert.AlertType.ERROR);
        }
    }

    private void loadCategories() {
        String sql = "SELECT name FROM categories WHERE user_id = ?";
        try (Connection conn = DatabaseManager.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                categoryList.getItems().add(rs.getString("name"));
            }
        } catch (SQLException e) {
            log.error("Failed to fetch categories", e);
            showAlert(ERROR, "Failed to fetch categories", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleAddCategory() {
        if (newCategoryName.getText().isEmpty()) {
            return;
        }
        String sql = "INSERT INTO categories (name, user_id) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newCategoryName.getText());
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();

            // Publish to NATS
            publisher.publishToNATS(sql, Arrays.asList(newCategoryName.getText(), userId));

            categoryList.getItems().add(newCategoryName.getText());
            newCategoryName.clear();
        } catch (SQLException e) {
            log.error("Failed to create new category", e);
            showAlert(ERROR, "Failed to create new category", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleAddAsset() {
        TextInputDialog dialog = new TextInputDialog("New Asset");
        dialog.setTitle("Add New Asset");
        dialog.setHeaderText("Create a new asset account");
        dialog.setContentText("Enter account name:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            String sql = "INSERT INTO accounts (user_id, account_name, account_type, balance) VALUES (?, ?, 'Asset', 0.0)";
            try (Connection conn = DatabaseManager.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                pstmt.setString(2, name);
                pstmt.executeUpdate();
                // Publish to NATS
                publisher.publishToNATS(sql, Arrays.asList(userId, name));
                // TODO: Investigate weird account type hardcode
                assetsTable.getItems().add(new Account(name, "Asset", 0.0));
            } catch (SQLException e) {
                log.error("Failed to create new asset account", e);
                showAlert(ERROR, "Failed to create new asset account", Alert.AlertType.ERROR);
            }
        });
    }

    @FXML
    private void handleDeleteAsset() {
        Account selected = assetsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            String sql = "DELETE FROM accounts WHERE account_name = ? AND user_id = ?";
            try (Connection conn = DatabaseManager.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, selected.getAccountName());
                pstmt.setInt(2, userId);
                pstmt.executeUpdate();

                // Publish to NATS
                publisher.publishToNATS(sql, Arrays.asList(selected.getAccountName(), userId));

                assetsTable.getItems().remove(selected);
            } catch (SQLException e) {
                log.error("Failed to delete asset account", e);
                showAlert(ERROR, "Failed to delete asset account", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleDeleteCategory() {
        String selectedCategory = categoryList.getSelectionModel().getSelectedItem();
        if (selectedCategory == null) {
            showAlert(ERROR, "No category selected", Alert.AlertType.ERROR);
            return;
        }

        try {
            deleteCategory(selectedCategory);
        } catch (SQLException e) {
            log.error("Failed to delete category", e);
            showAlert(ERROR, "Failed to delete category: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void deleteCategory(String category) throws SQLException {
        String sql = "DELETE FROM categories WHERE name = ? AND user_id = ?";
        try (Connection conn = DatabaseManager.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, category);
            pstmt.setInt(2, userId);
            int affectedRows = pstmt.executeUpdate();

            // Publish to NATS
            publisher.publishToNATS(sql, Arrays.asList(category, userId));

            if (affectedRows > 0) {
                categoryList.getItems().remove(category);
            } else {
                showAlert("Information", "No category was deleted.", Alert.AlertType.INFORMATION);
            }
        }
    }

    // Additional methods to manage assets like updating balances, etc.
    private void fetchUserId(String userName) {
        String sql = "SELECT user_id FROM users WHERE username = ?";
        try (Connection conn = DatabaseManager.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                userId = rs.getInt("user_id");
            }
        } catch (SQLException e) {
            log.error("Failed to fetch user ID", e);
            showAlert(ERROR, "Failed to fetch user ID", Alert.AlertType.ERROR);
        }
    }
}
