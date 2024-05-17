DROP TABLE IF EXISTS taxes CASCADE;
DROP TABLE IF EXISTS valuations CASCADE;
DROP TABLE IF EXISTS imports CASCADE;
DROP TABLE IF EXISTS exchange_rates CASCADE;
DROP TABLE IF EXISTS notifications CASCADE;
DROP TABLE IF EXISTS reports CASCADE;
DROP TABLE IF EXISTS recurring_transactions CASCADE;
DROP TABLE IF EXISTS budgets CASCADE;
DROP TABLE IF EXISTS transactions CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS account_balances CASCADE;
DROP TABLE IF EXISTS account_other_assets CASCADE;
DROP TABLE IF EXISTS account_loans CASCADE;
DROP TABLE IF EXISTS account_investments CASCADE;
DROP TABLE IF EXISTS account_depositories CASCADE;
DROP TABLE IF EXISTS account_credits CASCADE;
DROP TABLE IF EXISTS accounts CASCADE;
DROP TABLE IF EXISTS families CASCADE;
DROP TABLE IF EXISTS users CASCADE;

DROP TYPE IF EXISTS import_status;
DROP TYPE IF EXISTS account_status;

CREATE TYPE account_status AS ENUM ('ok', 'syncing', 'error');
CREATE TYPE import_status AS ENUM ('pending', 'importing', 'complete', 'failed');

CREATE TABLE families
(
    family_id  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name       TEXT,
    created_at TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    currency   TEXT             DEFAULT 'RUB'
);

CREATE TABLE users
(
    user_id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    family_id       UUID,
    first_name      TEXT,
    last_name       TEXT,
    email           TEXT UNIQUE,
    password_digest TEXT,
    created_at      TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    last_login      TIMESTAMP,
    active          BOOLEAN          DEFAULT true NOT NULL,
    last_synced_at  TIMESTAMP,
    FOREIGN KEY (family_id) REFERENCES families (family_id)
);

CREATE TABLE accounts
(
    account_id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id          UUID,
    family_id        UUID,
    account_type     TEXT                          NOT NULL,
    subtype          TEXT,
    account_name     TEXT,
    created_at       TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    accountable_type TEXT,
    accountable_id   UUID,
    balance          BIGINT           DEFAULT 0,
    currency         TEXT             DEFAULT 'RUB',
    is_active        BOOLEAN          DEFAULT true NOT NULL,
    status           account_status   DEFAULT 'ok' NOT NULL,
    sync_warnings    JSONB            DEFAULT '[]' NOT NULL,
    sync_errors      JSONB            DEFAULT '[]' NOT NULL,
    last_synced_at   TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    FOREIGN KEY (family_id) REFERENCES families (family_id),
    CHECK (account_type IN ('Checking', 'Savings', 'Investment', 'Credit', 'Credit Card', 'Debt', 'Asset'))
);

CREATE TABLE account_credits
(
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id      UUID NOT NULL,
    credit_limit    BIGINT,        -- Maximum credit limit
    interest_rate   NUMERIC(5, 2), -- Annual interest rate
    due_date        DATE,          -- Due date for payments
    minimum_payment BIGINT,        -- Minimum payment required
    created_at      TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    last_synced_at  TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts (account_id) ON DELETE CASCADE
);

CREATE TABLE account_depositories
(
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id      UUID NOT NULL,
    bank_name       TEXT,          -- Name of the bank
    account_number  TEXT,          -- Account number
    routing_number  TEXT,          -- Routing number
    interest_rate   NUMERIC(5, 2), -- Annual interest rate for savings accounts
    overdraft_limit BIGINT,        -- Overdraft limit if applicable
    created_at      TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    last_synced_at  TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts (account_id) ON DELETE CASCADE
);

CREATE TABLE account_investments
(
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id      UUID NOT NULL,
    investment_type TEXT,          -- Type of investment (e.g., stocks, bonds)
    market_value    BIGINT,        -- Current market value
    purchase_price  BIGINT,        -- Original purchase price
    purchase_date   DATE,          -- Date of purchase
    dividends       BIGINT,        -- Dividends received
    interest_rate   NUMERIC(5, 2), -- Annual interest rate if applicable
    created_at      TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    last_synced_at  TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts (account_id) ON DELETE CASCADE
);

CREATE TABLE account_loans
(
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id          UUID NOT NULL,
    loan_amount         BIGINT,        -- Total loan amount
    outstanding_balance BIGINT,        -- Current outstanding balance
    interest_rate       NUMERIC(5, 2), -- Annual interest rate
    loan_term           TEXT,          -- Term of the loan (e.g., 30 years)
    due_date            DATE,          -- Due date for payments
    payment_frequency   TEXT,          -- Payment frequency (e.g., monthly, bi-weekly)
    collateral          TEXT,          -- Collateral if applicable
    created_at          TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    last_synced_at      TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts (account_id) ON DELETE CASCADE
);

CREATE TABLE account_other_assets
(
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id        UUID NOT NULL,
    asset_type        TEXT,          -- Type of asset (e.g., real estate, vehicles)
    purchase_price    BIGINT,        -- Original purchase price
    current_value     BIGINT,        -- Current market value
    purchase_date     DATE,          -- Date of purchase
    depreciation_rate NUMERIC(5, 2), -- Annual depreciation rate if applicable
    created_at        TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    last_synced_at    TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts (account_id) ON DELETE CASCADE
);

CREATE TABLE account_balances
(
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id UUID                           NOT NULL,
    date       DATE                           NOT NULL,
    balance    BIGINT                         NOT NULL,
    currency   TEXT             DEFAULT 'RUB' NOT NULL,
    created_at TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts (account_id) ON DELETE CASCADE,
    UNIQUE (account_id, date, currency)
);

CREATE TABLE categories
(
    category_id    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name           TEXT NOT NULL,
    family_id      UUID,
    created_at     TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    last_synced_at TIMESTAMP,
    FOREIGN KEY (family_id) REFERENCES families (family_id),
    UNIQUE (family_id, name)
);

CREATE TABLE transactions
(
    transaction_id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name             TEXT,
    date             TIMESTAMP                      NOT NULL,
    amount           BIGINT                         NOT NULL,
    currency         TEXT             DEFAULT 'RUB' NOT NULL,
    account_id       UUID                           NOT NULL,
    category_id      UUID,
    excluded         BOOLEAN          DEFAULT false,
    notes            TEXT,
    transaction_type TEXT                           NOT NULL CHECK (transaction_type IN ('inflow', 'outflow')),

    created_at       TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    last_synced_at   TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts (account_id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories (category_id) ON DELETE SET NULL
);


CREATE TABLE budgets
(
    budget_id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID,
    category_id     UUID,
    limit_amount    BIGINT NOT NULL,
    start_date      TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    end_date        TIMESTAMP,
    created_at      TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    last_synced_at  TIMESTAMP,
    alert_threshold BIGINT           DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories (category_id) ON DELETE CASCADE
);

CREATE TABLE recurring_transactions
(
    recurring_transaction_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id                  UUID   NOT NULL,
    amount                   BIGINT NOT NULL,
    category_id              UUID,
    description              TEXT,
    frequency                BIGINT NOT NULL,
    created_at               TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    updated_at               TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    last_synced_at           TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories (category_id) ON DELETE SET NULL
);

CREATE TABLE reports
(
    report_id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID NOT NULL,
    report_type TEXT NOT NULL,
    date_range  TEXT NOT NULL,
    created_at  TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE notifications
(
    notification_id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id           UUID   NOT NULL,
    notification_type TEXT   NOT NULL,
    threshold         BIGINT NOT NULL,
    created_at        TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    last_synced_at    TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE exchange_rates
(
    id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    base_currency      TEXT NOT NULL,
    converted_currency TEXT NOT NULL,
    rate               BIGINT,
    date               DATE,
    created_at         TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (base_currency, converted_currency, date)
);

CREATE TABLE imports
(
    id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id         UUID NOT NULL,
    column_mappings    JSONB,
    status             import_status    DEFAULT 'pending',
    raw_csv_str        TEXT,
    normalized_csv_str TEXT,
    created_at         TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts (account_id)
);

CREATE TABLE valuations
(
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id UUID                           NOT NULL,
    date       DATE                           NOT NULL,
    value      BIGINT                         NOT NULL,
    currency   TEXT             DEFAULT 'RUB' NOT NULL,
    created_at TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (account_id, date),
    FOREIGN KEY (account_id) REFERENCES accounts (account_id) ON DELETE CASCADE
);

CREATE TABLE taxes
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        TEXT                           NOT NULL,
    description TEXT,
    rate        BIGINT                         NOT NULL,
    currency    TEXT             DEFAULT 'RUB' NOT NULL,
    created_at  TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP        DEFAULT CURRENT_TIMESTAMP
);

-- Foreign key constraints
ALTER TABLE valuations
    ADD CONSTRAINT fk_valuations_accounts FOREIGN KEY (account_id) REFERENCES accounts (account_id) ON DELETE CASCADE;
ALTER TABLE imports
    ADD CONSTRAINT fk_imports_accounts FOREIGN KEY (account_id) REFERENCES accounts (account_id);
ALTER TABLE transactions
    ADD CONSTRAINT fk_transactions_accounts FOREIGN KEY (account_id) REFERENCES accounts (account_id) ON DELETE CASCADE;
ALTER TABLE transactions
    ADD CONSTRAINT fk_transactions_categories FOREIGN KEY (category_id) REFERENCES categories (category_id) ON DELETE SET NULL;
ALTER TABLE users
    ADD CONSTRAINT fk_users_families FOREIGN KEY (family_id) REFERENCES families (family_id);
