package com.github.kxrxh.javalin.rest.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BudgetSuggestions {
    private long currentLimit;
    private long suggestedLimit;
    private String suggestedInterval;

}
