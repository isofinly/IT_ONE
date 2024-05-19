package com.github.kxrxh.javalin.rest.entities;

import com.github.kxrxh.javalin.rest.database.models.Transaction;
import com.github.kxrxh.javalin.rest.services.TransactionService;
import io.javalin.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

@Slf4j
public class MockBankIntegration extends AbstractBankIntegration {

    @Override
    protected String getBankApiUrl() {
        String bankUrl = System.getenv("MOCK_BANK_URL") != null ? System.getenv("MOCK_BANK_URL")
                : "http://localhost:8080";
        return String.format("https://%s/api/tables/transactions", bankUrl);
    }

    @Override
    public void integrateWithBank(UUID userId, String bankCredentials) throws ValidationException {
        try {
            List<Transaction> transactions = fetchTransactions(bankCredentials);

            for (Transaction transaction : transactions) {
                TransactionService.createTransaction(userId, transaction.getAccountId(), transaction.getCategoryId(),
                        transaction.getAmount(), transaction.getName(),
                        transaction.getDate(), transaction.getCurrency(),
                        transaction.getNotes(), transaction.getTransactionType());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
