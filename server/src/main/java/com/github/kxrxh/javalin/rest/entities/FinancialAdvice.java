package com.github.kxrxh.javalin.rest.entities;

import com.github.kxrxh.javalin.rest.database.models.Transaction;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class FinancialAdvice {
    private List<Transaction> recentTransactions;
    private String advice;
}
