package com.github.kxrxh.javalin.rest.entities;

import java.util.List;
import java.util.UUID;

import com.github.kxrxh.javalin.rest.database.models.Transaction;
import com.github.kxrxh.javalin.rest.services.TransactionService;

public class MockBankIntegration extends AbstractBankIntegration {

    @Override
    protected String getBankApiUrl() {
        String bankUrl = System.getenv("MOCK_BANK_URL") != null ? System.getenv("MOCK_BANK_URL")
                : "http://localhost:8080";
        return String.format("http://%s/api/tables/transactions", bankUrl);
    }

    @Override
    public void integrateWithBank(UUID userId, String bankCredentials) throws Exception {
        try {
            List<Transaction> transactions = fetchTransactions(bankCredentials);

            for (Transaction transaction : transactions) {
                TransactionService.createTransaction(userId, transaction.getAccountId(), transaction.getCategoryId(),
                        transaction.getAmount(), transaction.getName(),
                        transaction.getDate(), transaction.getCurrency(),
                        transaction.getNotes(), transaction.getTransactionType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
