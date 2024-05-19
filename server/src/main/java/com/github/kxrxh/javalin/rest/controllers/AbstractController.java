package com.github.kxrxh.javalin.rest.controllers;

public abstract class AbstractController {
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
    protected static final String VALUATION_ID = "valuation_id";
    protected static final String AMOUNT = "amount";
    protected static final String NAME = "name";
    protected static final String DATE = "date";
    protected static final String DATE_STR = "date_str";
    protected static final String CURRENCY = "currency";
    protected static final String NOTES = "notes";
    protected static final String TRANSACTION_TYPE = "transaction_type";
    protected static final String USER_ID = "user_id";
    protected static final String EMAIL = "email";
    protected static final String PASSWORD_DIGEST = "password_digest";
    protected static final String FIRST_NAME = "first_name";
    protected static final String LAST_NAME = "last_name";
    protected static final String FAMILY_ID = "family_id";
    protected static final String CURRENCY_CODE = "currency_code";
    protected static final String RECURRING_TRANSACTION_ID = "currency_code";


    protected AbstractController() {
    }
}
