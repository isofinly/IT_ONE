package com.github.kxrxh.javalin.rest.services;


public abstract class AbstractService{
    protected static final String WRONG_BODY_FORMAT = "Wrong body format: ";
    protected static final String MISSING_REQUIERED_STRING = "Missing required parameters";
    protected static final String INTERNAL_ERROR = "Internal Server Error: ";
    protected static final String CREDIT_ID = "credit_id";
    protected static final String DEPOSITORY_ID = "depository_id";
    protected static final String INVESTMENT_ID = "investment_id";
    protected static final String LOAN_ID = "loan_id";
    protected static final String ASSET_ID = "asset_id";
    protected static final String CATEGORY_ID = "category_id";
    protected static final String BASE_CURRENCY = "base_currency";
    protected static final String CONVERTED_CURRENCY = "converted_currency";
    protected static final String TAX_ID = "tax_id";
    protected static final String ACCOUNT_ID = "account_id";
    protected static final String BALANCE_ID = "balance_id";


    protected AbstractService() {
    }
}
