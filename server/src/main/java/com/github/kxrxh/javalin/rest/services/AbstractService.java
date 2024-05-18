package com.github.kxrxh.javalin.rest.services;


public abstract class AbstractService {
    protected static final String WRONG_BODY_FORMAT = "Wrong body format: ";
    protected static final String MISSING_REQUIERED_STRING = "Missing required parameters";
    protected static final String INTERNAL_ERROR = "Internal Server Error: ";
    protected static final String CREDIT_ID = "credit_id";
    protected static final String USER_ID = "user_id";
    protected static final String DEPOSITORY_ID = "depository_id";
    protected static final String TRANSACTION_ID = "transaction_id";
    protected static final String INVESTMENT_ID = "investment_id";
    protected static final String LOAN_ID = "loan_id";
    protected static final String ASSET_ID = "asset_id";
    protected static final String CATEGORY_ID = "category_id";
    protected static final String BASE_CURRENCY = "base_currency";
    protected static final String CONVERTED_CURRENCY = "converted_currency";
    protected static final String TAX_ID = "tax_id";
    protected static final String NAME = "name";
    protected static final String DATE = "date";
    protected static final String AMOUNT = "amount";
    protected static final String CURRENCY = "currency";
    protected static final String ACCOUNT_ID = "account_id";
    protected static final String BALANCE_ID = "balance_id";
    protected static final String BUDGET_ID = "budget_id";
    protected static final String LAST_SYNCED_AT = "last_synced_at";
    protected static final String TOTAL_EXPENSES = "totalExpenses";
    protected static final String DUE_DATE = "due_date";
    protected static final String ON_AL_ACCOUNT_ID_A_ACCOUNT_ID = "JOIN accounts a ON al.account_id = a.account_id ";
    protected static final String NO_FAMILY_FOUND = "No family found for the given user.";


    protected AbstractService() {
    }
}
