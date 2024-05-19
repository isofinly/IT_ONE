# REST API Documentation

## ENV variables

- **NATS_URL**: nats://nats:4222
- **DATABASE_URL**: jdbc:postgresql://database:5432/postgres?ssl=on&sslrootcert=cert/ca-cert.pem
- **DATABASE_USER**: postgres
- **DATABASE_PASSWORD**: pgpwd
- **PORT**: 3030
- **JWT_SECRET**: "secret"
- **KEY_PASSWORD**: "password"
- **STORE_PASSWORD**: "password"

## Routes

### Authentication Routes

#### Authentication Setup

- **POST** `/api/v1/login`
  - Login as user and get auth token
- **POST** `/api/v1/register`
  - Create new user

**Body**:

```json
{
    "email": "somemail@example.com",
    "password": "coolpass"
}
```

---

### Account Routes

#### Transfer Funds

- **POST** `/api/v1/accounts/transfer`
  - Transfer funds between accounts.

#### Merge Accounts

- **POST** `/api/v1/accounts/merge`
  - Merge accounts.

#### Balance Operations

- **POST** `/api/v1/accounts/create_balance`
  - Create a new balance.
- **GET** `/api/v1/accounts/read_balance`
  - Read balance information.
- **PUT** `/api/v1/accounts/update_balance`
  - Update balance details.
- **DELETE** `/api/v1/accounts/delete_balance`
  - Delete a balance.
- **GET** `/api/v1/accounts/calculate_total_balance`
  - Calculate total balance.

#### Credit Operations

- **POST** `/api/v1/accounts/create_credit`
  - Create a new credit account.
- **GET** `/api/v1/accounts/read_credit`
  - Read credit account details.
- **PUT** `/api/v1/accounts/update_credit`
  - Update credit account information.
- **DELETE** `/api/v1/accounts/delete_credit`
  - Delete a credit account.
- **GET** `/api/v1/accounts/update_credit_limit`
  - Update credit limit and calculate interest.

#### Depository Operations

- **POST** `/api/v1/accounts/create_depository`
  - Create a new depository account.
- **GET** `/api/v1/accounts/read_depository`
  - Read depository account details.
- **PUT** `/api/v1/accounts/update_depository`
  - Update depository account information.
- **DELETE** `/api/v1/accounts/delete_depository`
  - Delete a depository account.
- **GET** `/api/v1/accounts/calculate_depository_interest`
  - Calculate depository interest.

#### Investment Operations

- **POST** `/api/v1/accounts/create_investment`
  - Create a new investment account.
- **GET** `/api/v1/accounts/read_investment`
  - Read investment account details.
- **PUT** `/api/v1/accounts/update_investment`
  - Update investment account information.
- **DELETE** `/api/v1/accounts/delete_investment`
  - Delete an investment account.
- **GET** `/api/v1/accounts/calculate_dividends`
  - Calculate dividends.

#### Loan Operations

- **POST** `/api/v1/accounts/create_loan`
  - Create a new loan account.
- **GET** `/api/v1/accounts/read_loan`
  - Read loan account details.
- **PUT** `/api/v1/accounts/update_loan`
  - Update loan account information.
- **DELETE** `/api/v1/accounts/delete_loan`
  - Delete a loan account.
- **GET** `/api/v1/accounts/calculate_loan_interest`
  - Calculate loan interest.
- **GET** `/api/v1/accounts/check_due_date_notifications`
  - Check due date notifications for loans.

#### Other Asset Operations

- **POST** `/api/v1/accounts/create_asset`
  - Create a new other asset.
- **GET** `/api/v1/accounts/read_asset`
  - Read other asset details.
- **PUT** `/api/v1/accounts/update_asset`
  - Update other asset information.
- **DELETE** `/api/v1/accounts/delete_asset`
  - Delete an other asset.
- **DELETE** `/api/v1/accounts/apply_depreciation`
  - Apply depreciation to other assets.

---

### Advice Routes

#### Financial Advice

- **GET** `/api/v1/advice`
  - Get financial advice.

#### Financial Forecast

- **GET** `/api/v1/financial_forecast`
  - Get financial forecast.

---

### Budget Routes

#### Budget Alert

- **POST** `/api/v1/budget/set_budget_alert`
  - Set budget alerts.

#### Analyze Budget

- **GET** `/api/v1/budget/analyze_budget`
  - Analyze budget.

---

### Category Routes

#### Category Analysis

- **GET** `/api/v1/category/analysis`
  - Analyze categories.

#### Category CRUD Operations

- **POST** `/api/v1/category/create`
  - Create a new category.
- **GET** `/api/v1/category/read`
  - Read category details.
- **PUT** `/api/v1/category/update`
  - Update category information.
- **DELETE** `/api/v1/category/delete`
  - Delete a category.

---

### Integration Routes

#### Bank Integration

- **POST** `/api/v1/integration/bank`
  - Integrate with a bank.

#### Auto-Categorization

- **POST** `/api/v1/integration/auto-categorize`
  - Auto-categorize transactions.

---

### Report Routes

#### Monthly Report

- **GET** `/api/v1/monthly_report`
  - Generate a monthly report.

---

### Tax Routes

#### Tax CRUD Operations

- **POST** `/api/v1/tax/create`
  - Create a new tax.
- **GET** `/api/v1/tax/read`
  - Read tax details.
- **PUT** `/api/v1/tax/update`
  - Update tax information.
- **DELETE** `/api/v1/tax/delete`
  - Delete a tax.
- **GET** `/api/v1/tax/calculate`
  - Calculate taxes.

---

### Transaction Routes

#### Recurring Transaction Operations

- **POST** `/api/v1/transaction/recurring/create`
  - Create a recurring transaction.
- **GET** `/api/v1/transaction/recurring/read`
  - Read recurring transaction details.
- **PUT** `/api/v1/transaction/recurring/update`
  - Update recurring transaction information.
- **DELETE** `/api/v1/transaction/recurring/delete`
  - Delete a recurring transaction.
- **POST** `/api/v1/transaction/recurring`
  - Create a recurring transaction.

#### Exchange Rate Operations

- **GET** `/api/v1/exchange_rate/create`
  - Create an exchange rate.
- **GET** `/api/v1/exchange_rate/read`
  - Read exchange rate details.
- **PUT** `/api/v1/exchange_rate/update`
  - Update exchange rate information.
- **DELETE** `/api/v1/exchange_rate/delete`
  - Delete an exchange rate.

#### Transaction Operations

- **GET** `/api/v1/transaction/search`
  - Search transactions.
- **POST** `/api/v1/transaction/create`
  - Create a transaction.
- **PUT** `/api/v1/transaction/update`
  - Update transaction details.
- **DELETE** `/api/v1/transaction/delete`
  - Delete a transaction.

---

### Valuation Routes

#### Valuation Operations

- **POST** `/api/v1/valuation/create`
  - Create a valuation.
- **GET** `/api/v1/valuation/read`
  - Read valuation details.
- **PUT** `/api/v1/valuation/update`
  - Update valuation information.
- **DELETE** `/api/v1/valuation/delete`
  - Delete a valuation.

---
