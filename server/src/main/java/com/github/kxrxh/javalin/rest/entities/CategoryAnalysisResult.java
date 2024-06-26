package com.github.kxrxh.javalin.rest.entities;

import com.github.kxrxh.javalin.rest.database.models.Transaction;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CategoryAnalysisResult {
    private List<Transaction> transactions;
    private long totalAmount;

}