-- Insert mock data into the users table
INSERT INTO users (username, email, password_hash)
VALUES ('user1', 'user1@example.com', 'hash1'),
       ('user2', 'user2@example.com', 'hash2'),
       ('user3', 'user3@example.com', 'hash3'),
       ('user4', 'user4@example.com', 'hash4'),
       ('user5', 'user5@example.com', 'hash5');

-- Insert mock data into the accounts table
INSERT INTO accounts (user_id, account_type, balance, account_name)
VALUES (1, 'Checking', 1000, 'Checking'),
       (1, 'Savings', 5000, 'Savings'),
       (2, 'Debt', -500, 'Credit Card'),
       (2, 'Asset', 10000, 'Account'),
       (4, 'Checking', 2000, 'Checking'),
       (5, 'Savings', 3000, 'Savings');

-- Insert mock data into the transactions table
INSERT INTO transactions (amount, category_id, user_id, description)
VALUES (5000, 1, 1, 'Monthly salary'),
       (-1500, 2, 1, 'Grocery shopping'),
       (2000, 1, 1, 'Freelance project'),
       (-100, 3, 1, 'Monthly savings'),
       (3000, 4, 1, 'Stock investment');


-- Insert mock data into the local_groups table
INSERT INTO local_groups (name)
VALUES ('Group A'),
       ('Group B'),
       ('Group C');

-- Insert mock data into the group_members table
INSERT INTO group_members (group_id, user_id)
VALUES (1, 1),
       (1, 2),
       (1, 3),
       (2, 2),
       (2, 4),
       (3, 3),
       (3, 5);

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
       (-1500, '2024-05-03 07:14:53', 2, 1, 'Grocery shopping'),
       (2000, '2024-02-01 07:14:53', 1, 1, 'Freelance project'),
       (-100, '2024-02-03 07:14:53', 3, 1, 'Monthly savings'),
       (3000, '2024-03-02 07:14:53', 4, 1, 'Stock investment');
