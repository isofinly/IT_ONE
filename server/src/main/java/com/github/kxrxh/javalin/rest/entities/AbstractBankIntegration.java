package com.github.kxrxh.javalin.rest.entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kxrxh.javalin.rest.database.models.Transaction;
import com.github.kxrxh.javalin.rest.interfaces.BankIntegration;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class AbstractBankIntegration implements BankIntegration {

    protected abstract String getBankApiUrl();

    @Override
    public List<Transaction> fetchTransactions(String bankCredentials) throws Exception {
        URL url = new URL(getBankApiUrl());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + bankCredentials);

        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            ObjectMapper mapper = new ObjectMapper();
            JSONArray jsonArray = new JSONArray(mapper.readValue(conn.getInputStream(), String.class));
            List<Transaction> transactions = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Transaction transaction = Transaction.builder()
                        .transactionId(UUID.fromString(jsonObject.getString("transaction_id")))
                        .name(jsonObject.getString("bank_name"))
                        .accountId(UUID.fromString(jsonObject.getString("account_number")))
                        .date(LocalDateTime.parse(jsonObject.getString("transaction_date")))
                        .amount(jsonObject.getLong("amount"))
                        .currency(jsonObject.getString("currency"))
                        .transactionType(Transaction.TransactionType.valueOf(jsonObject.getString("transaction_type")))
                        .build();
                transactions.add(transaction);
            }
            return transactions;
        } else {
            throw new FetchingException(conn.getResponseMessage());
        }
    }

    class FetchingException extends RuntimeException {
        public FetchingException(String message) {
            super("Failed to fetch transactions: " + message);
        }
    }
}
