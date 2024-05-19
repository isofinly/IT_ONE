package com.pivo.app;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;

public class Account {
    private final SimpleStringProperty accountName;
    private final SimpleStringProperty accountType;
    private final SimpleDoubleProperty balance;

    public Account(String accountName, String accountType, double balance) {
        this.accountName = new SimpleStringProperty(accountName);
        this.accountType = new SimpleStringProperty(accountType);
        this.balance = new SimpleDoubleProperty(balance);
    }

    public String getAccountName() {
        return accountName.get();
    }

    public void setAccountName(String accountName) {
        this.accountName.set(accountName);
    }

    public String getAccountType() {
        return accountType.get();
    }

    public void setAccountType(String accountType) {
        this.accountType.set(accountType);
    }

    public double getBalance() {
        return balance.get();
    }

    public void setBalance(double balance) {
        this.balance.set(balance);
    }

    public ObservableValue<String> accountNameProperty() {
        return accountName;
    }

    public ObservableValue<String> accountTypeProperty() {
        return accountType;
    }

    public ObservableValue<Number> balanceProperty() {
        return balance;
    }
}
