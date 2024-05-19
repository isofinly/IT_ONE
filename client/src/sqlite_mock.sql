-- Insert mock data into the users table
INSERT INTO users (username, email, password_hash)
VALUES ('user1', 'user1@example.com', '|$2a$12$/ZI1lUBbu3K.0BqH6ElGeOTE1uSU4zfyid34CocACqkF68AT5rO2G'), -- password is '1234'
       ('user2', 'user2@example.com', '|$2a$12$/ZI1lUBbu3K.0BqH6ElGeOTE1uSU4zfyid34CocACqkF68AT5rO2G'), -- password is '1234'
       ('user3', 'user3@example.com', '|$2a$12$/ZI1lUBbu3K.0BqH6ElGeOTE1uSU4zfyid34CocACqkF68AT5rO2G'), -- password is '1234'
       ('user4', 'user4@example.com', '|$2a$12$/ZI1lUBbu3K.0BqH6ElGeOTE1uSU4zfyid34CocACqkF68AT5rO2G'), -- password is '1234'
       ('user5', 'user5@example.com', '|$2a$12$/ZI1lUBbu3K.0BqH6ElGeOTE1uSU4zfyid34CocACqkF68AT5rO2G'); -- password is '1234'

-- Insert mock data into the accounts table
INSERT INTO accounts (user_id, account_type, balance, account_name)
VALUES (1, 'Checking', 1000, 'Checking'),
       (1, 'Savings', 5000, 'Savings'),
       (2, 'Debt', -500, 'Credit Card'),
       (2, 'Asset', 10000, 'Account'),
       (4, 'Checking', 2000, 'Checking'),
       (5, 'Savings', 3000, 'Savings');

INSERT INTO accounts (user_id, account_type, balance, account_name)
VALUES (1, 'Debt', -500, 'Credit Card'),
       (1, 'Asset', 10000, 'Account');

-- Insert mock data into the transactions table
INSERT INTO transactions (amount, category_id, user_id, description)
VALUES (5000, 1, 1, 'Monthly salary'),
       (-1500, 2, 1, 'Grocery shopping'),
       (2000, 1, 1, 'Freelance project'),
       (2000, 1, 2, 'Freelance project'),
       (-100, 3, 1, 'Monthly savings'),
       (-100, 3, 3, 'Monthly savings'),
       (-100, 3, 3, 'Monthly savings'),
       (-100, 3, 3, 'Monthly savings'),
       (3000, 4, 1, 'Stock investment');

INSERT INTO categories (name, user_id)
VALUES ('Income', 1),
       ('Spending', 1),
       ('Savings', 1),
       ('Investing', 1),
       ('Income', 2),
       ('Spending', 2),
       ('Savings', 2),
       ('Investing', 2);

INSERT INTO transactions (amount, transaction_date, category_id, user_id, description)
VALUES (5000, '2024-05-04 07:14:53', 1, 1, 'Monthly salary'),
       (-1500, '2024-05-03 07:14:53', 2, 3, 'Grocery shopping'),
       (2000, '2024-02-01 07:14:53', 1, 2, 'Freelance project'),
       (-100, '2024-02-03 07:14:53', 3, 1, 'Monthly savings'),
       (3000, '2024-03-02 07:14:53', 4, 5, 'Stock investment');
