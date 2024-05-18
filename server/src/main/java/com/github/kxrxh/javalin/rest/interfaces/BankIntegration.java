package com.github.kxrxh.javalin.rest.interfaces;

import com.github.kxrxh.javalin.rest.database.ConnectionRetrievingException;
import com.github.kxrxh.javalin.rest.database.models.Transaction;
import io.javalin.validation.ValidationException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

public interface BankIntegration {
    void integrateWithBank(UUID userId, String bankCredentials) throws ValidationException;

    List<Transaction> fetchTransactions(String bankCredentials) throws ConnectionRetrievingException, IOException, URISyntaxException;
}

