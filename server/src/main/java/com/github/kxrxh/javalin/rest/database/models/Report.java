package com.github.kxrxh.javalin.rest.database.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Report {

    private Long reportId;
    private Long userId;
    private String reportType;
    private String dateRange;
    private List<Transaction> transactions;

}
