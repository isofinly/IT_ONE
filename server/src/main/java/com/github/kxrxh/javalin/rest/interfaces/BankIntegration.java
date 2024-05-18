package com.github.kxrxh.javalin.rest.interfaces;

import com.github.kxrxh.javalin.rest.database.models.Transaction;

import java.util.List;
import java.util.UUID;

public interface BankIntegration {
    void integrateWithBank(UUID userId, String bankCredentials) throws Exception;

    List<Transaction> fetchTransactions(String bankCredentials) throws Exception;
}

