DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS accounts CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS transactions CASCADE;
DROP TABLE IF EXISTS budgets CASCADE;
DROP TABLE IF EXISTS recurring_transactions CASCADE;
DROP TABLE IF EXISTS reports CASCADE;
DROP TABLE IF EXISTS notifications CASCADE;

CREATE TABLE users
(
    user_id        SERIAL PRIMARY KEY,
    username       TEXT NOT NULL UNIQUE,
    email          TEXT UNIQUE,
    password       TEXT NOT NULL,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login     TIMESTAMP,
    last_synced_at TIMESTAMP -- New field for synchronization
);

CREATE TABLE accounts
(
    account_id   SERIAL PRIMARY KEY,
    user_id      BIGINT,
    account_type TEXT   NOT NULL, -- e.g., 'Checking', 'Savings', 'Investment', 'Credit'
    balance      BIGINT NOT NULL,
    account_name TEXT   NOT NULL, -- e.g., 'Bank of America - Checking'
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    CHECK (account_type IN ('Checking', 'Savings', 'Investment', 'Credit', 'Credit Card', 'Debt', 'Asset'))
);

CREATE TABLE categories
(
    category_id SERIAL PRIMARY KEY,
    name        TEXT NOT NULL,
    user_id     BIGINT,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE transactions
(
    transaction_id   SERIAL PRIMARY KEY,
    amount           BIGINT NOT NULL,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    category_id      INTEGER,
    user_id          BIGINT,
    description      TEXT,
    FOREIGN KEY (category_id) REFERENCES categories (category_id) ON DELETE SET NULL,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE budgets
(
    budget_id    SERIAL PRIMARY KEY,
    user_id      BIGINT,
    category_id  INTEGER,
    limit_amount BIGINT NOT NULL,
    start_date   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    end_date     TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories (category_id) ON DELETE CASCADE
);

CREATE TABLE recurring_transactions
(
    recurring_transaction_id SERIAL PRIMARY KEY,
    user_id                  BIGINT NOT NULL,
    amount                   BIGINT NOT NULL,
    category_id              BIGINT,
    description              TEXT,
    frequency                BIGINT   NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories (category_id) ON DELETE SET NULL
);

CREATE TABLE reports
(
    report_id   SERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    report_type TEXT   NOT NULL,
    date_range  TEXT   NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE notifications
(
    notification_id   SERIAL PRIMARY KEY,
    user_id           BIGINT NOT NULL,
    notification_type TEXT   NOT NULL,
    threshold         BIGINT NOT NULL,
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);

-- Add last_synced_at fields to other tables as needed

ALTER TABLE accounts
    ADD COLUMN last_synced_at TIMESTAMP;

ALTER TABLE transactions
    ADD COLUMN last_synced_at TIMESTAMP;

ALTER TABLE categories
    ADD COLUMN last_synced_at TIMESTAMP;

ALTER TABLE budgets
    ADD COLUMN last_synced_at TIMESTAMP;

ALTER TABLE recurring_transactions
    ADD COLUMN last_synced_at TIMESTAMP;

ALTER TABLE notifications
    ADD COLUMN last_synced_at TIMESTAMP;

ALTER TABLE budgets
    ADD COLUMN alert_threshold BIGINT DEFAULT 0;

