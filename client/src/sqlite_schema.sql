-- Creating the users table
CREATE TABLE users
(
    user_id       INTEGER PRIMARY KEY AUTOINCREMENT,
    username      TEXT NOT NULL UNIQUE,
    email         TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    created_at    DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_login    DATETIME
);

CREATE TABLE accounts
(
    account_id   INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id      INTEGER,
    account_type TEXT NOT NULL, -- e.g., 'Checking', 'Savings', 'Investment', 'Credit'
    balance      REAL NOT NULL,
    account_name TEXT NOT NULL, -- e.g., 'Bank of America - Checking'
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);

-- Creating the transactions table
CREATE TABLE transactions
(
    transaction_id   INTEGER PRIMARY KEY AUTOINCREMENT,
    amount           INTEGER NOT NULL,
    transaction_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    category_id      INTEGER,
    user_id          INTEGER,
    description      TEXT,
    FOREIGN KEY (category_id) REFERENCES categories (category_id) ON DELETE SET NULL,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);

-- Creating the categories table
CREATE TABLE categories
(
    category_id INTEGER PRIMARY KEY AUTOINCREMENT,
    name        TEXT NOT NULL,
    user_id     INTEGER,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);

-- Creating the budgets table
CREATE TABLE budgets
(
    budget_id    INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id      INTEGER,
    category_id  INTEGER,
    limit_amount INTEGER NOT NULL,
    start_date   DATETIME DEFAULT CURRENT_TIMESTAMP,
    end_date     DATETIME,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories (category_id) ON DELETE CASCADE
);

-- Creating the local_groups table
CREATE TABLE local_groups
(
    group_id   INTEGER PRIMARY KEY AUTOINCREMENT,
    name       TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Creating the group_members table
CREATE TABLE group_members
(
    group_id INTEGER,
    user_id  INTEGER,
    PRIMARY KEY (group_id, user_id),
    FOREIGN KEY (group_id) REFERENCES local_groups (group_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);


