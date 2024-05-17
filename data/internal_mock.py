import random
import string
import uuid
from datetime import datetime, timedelta


def generate_username():
    return ''.join(random.choice(string.ascii_lowercase) for _ in range(8))


def generate_email(username):
    domains = ['gmail.com', 'yahoo.com', 'hotmail.com', 'outlook.com']
    domain = random.choice(domains)
    return f"{username}@{domain}"


def generate_password_hash():
    return ''.join(random.choice(string.ascii_letters + string.digits) for _ in range(12))


def generate_timestamp():
    return datetime.now().strftime('%Y-%m-%d %H:%M:%S')


def generate_account_type():
    types = ['Checking', 'Savings', 'Investment', 'Credit', 'Credit Card', 'Debt', 'Asset']
    return random.choice(types)


def generate_account_name():
    banks = ['Bank of America', 'Chase', 'Wells Fargo', 'Citibank', 'US Bank']
    return f"{random.choice(banks)} - {generate_account_type()}"


def generate_amount():
    return random.randint(10, 10000)


def generate_description():
    descriptions = ['Supermarket', 'Gas Station', 'Restaurant', 'Online Shopping', 'Utilities']
    return random.choice(descriptions)


def generate_report_type():
    types = ['Monthly', 'Quarterly', 'Yearly']
    return random.choice(types)


def generate_date_range():
    end_date = datetime.now()
    start_date = end_date - timedelta(days=365)
    return f"{start_date.strftime('%Y-%m-%d')} to {end_date.strftime('%Y-%m-%d')}"


def generate_uuid():
    return str(uuid.uuid4())


def generate_sql_insert(table_name, values):
    columns = ', '.join(values.keys())
    vs = []
    for value in values.values():
        if isinstance(value, str):
            value = f"'{value}'"
        elif isinstance(value, datetime):
            value = f"'{value.strftime('%Y-%m-%d %H:%M:%S')}'"
        else:
            value = str(value)
        vs.append(value)
    placeholders = ', '.join(vs)
    return f"INSERT INTO {table_name} ({columns}) VALUES ({placeholders});"


def generate_users(num_users):
    users = []
    for _ in range(num_users):
        username = generate_username()
        user = {
            'user_id': generate_uuid(),
            'family_id': generate_uuid(),
            'first_name': ''.join(random.choice(string.ascii_lowercase) for _ in range(6)),
            'last_name': ''.join(random.choice(string.ascii_lowercase) for _ in range(6)),
            'email': generate_email(username),
            'password_digest': generate_password_hash(),
            'created_at': generate_timestamp(),
            'updated_at': generate_timestamp(),
            'last_login': generate_timestamp(),
            'last_prompted_upgrade_commit_sha': generate_password_hash(),
            'last_alerted_upgrade_commit_sha': generate_password_hash(),
            'active': True,
            'last_synced_at': generate_timestamp()
        }
        users.append(user)
    return users


def generate_accounts(num_accounts, user_ids):
    accounts = []
    for _ in range(num_accounts):
        account = {
            'account_id': generate_uuid(),
            'user_id': random.choice(user_ids),
            'family_id': generate_uuid(),
            'account_type': generate_account_type(),
            'subtype': 'subtype_placeholder',
            'account_name': generate_account_name(),
            'created_at': generate_timestamp(),
            'updated_at': generate_timestamp(),
            'accountable_type': 'AccountableType',
            'accountable_id': generate_uuid(),
            'balance': generate_amount(),
            'currency': 'USD',
            'is_active': True,
            'status': 'ok',
            'sync_warnings': '[]',
            'sync_errors': '[]',
            'last_sync_date': datetime.now().strftime('%Y-%m-%d'),
            'last_synced_at': generate_timestamp()
        }
        accounts.append(account)
    return accounts


def generate_categories(num_categories, user_ids):
    categories = []
    for _ in range(num_categories):
        category = {
            'category_id': generate_uuid(),
            'name': ''.join(random.choice(string.ascii_lowercase) for _ in range(6)),
            'family_id': generate_uuid(),
            'created_at': generate_timestamp(),
            'updated_at': generate_timestamp(),
            'last_synced_at': generate_timestamp()
        }
        categories.append(category)
    return categories


def generate_transactions(num_transactions, account_ids, category_ids):
    transactions = []
    for _ in range(num_transactions):
        transaction = {
            'transaction_id': generate_uuid(),
            'name': generate_description(),
            'date': datetime.now().strftime('%Y-%m-%d'),
            'amount': generate_amount(),
            'currency': 'USD',
            'account_id': random.choice(account_ids),
            'category_id': random.choice(category_ids),
            'excluded': False,
            'notes': generate_description(),
            'created_at': generate_timestamp(),
            'updated_at': generate_timestamp(),
            'last_synced_at': generate_timestamp()
        }
        transactions.append(transaction)
    return transactions


def generate_budgets(num_budgets, user_ids, category_ids):
    budgets = []
    for _ in range(num_budgets):
        budget = {
            'budget_id': generate_uuid(),
            'user_id': random.choice(user_ids),
            'category_id': random.choice(category_ids),
            'limit_amount': generate_amount(),
            'start_date': generate_timestamp(),
            'end_date': generate_timestamp(),
            'created_at': generate_timestamp(),
            'updated_at': generate_timestamp(),
            'last_synced_at': generate_timestamp(),
            'alert_threshold': generate_amount()
        }
        budgets.append(budget)
    return budgets


def generate_recurring_transactions(num_recurring_transactions, user_ids, category_ids):
    recurring_transactions = []
    for _ in range(num_recurring_transactions):
        recurring_transaction = {
            'recurring_transaction_id': generate_uuid(),
            'user_id': random.choice(user_ids),
            'amount': generate_amount(),
            'category_id': random.choice(category_ids),
            'description': generate_description(),
            'frequency': random.randint(1, 12),
            'created_at': generate_timestamp(),
            'updated_at': generate_timestamp(),
            'last_synced_at': generate_timestamp()
        }
        recurring_transactions.append(recurring_transaction)
    return recurring_transactions


def generate_reports(num_reports, user_ids):
    reports = []
    for _ in range(num_reports):
        report = {
            'report_id': generate_uuid(),
            'user_id': random.choice(user_ids),
            'report_type': generate_report_type(),
            'date_range': generate_date_range(),
            'created_at': generate_timestamp(),
            'updated_at': generate_timestamp()
        }
        reports.append(report)
    return reports


def generate_notifications(num_notifications, user_ids):
    notifications = []
    for _ in range(num_notifications):
        notification = {
            'notification_id': generate_uuid(),
            'user_id': random.choice(user_ids),
            'notification_type': 'Threshold Exceeded',
            'threshold': random.randint(100, 1000),
            'created_at': generate_timestamp(),
            'updated_at': generate_timestamp(),
            'last_synced_at': generate_timestamp()
        }
        notifications.append(notification)
    return notifications


def generate_account_balances(num_balances, account_ids):
    balances = []
    for _ in range(num_balances):
        balance = {
            'id': generate_uuid(),
            'account_id': random.choice(account_ids),
            'date': datetime.now().strftime('%Y-%m-%d'),
            'balance': generate_amount(),
            'currency': 'USD',
            'created_at': generate_timestamp(),
            'updated_at': generate_timestamp()
        }
        balances.append(balance)
    return balances


def generate_account_credits(num_credits, account_ids):
    credits = []
    for _ in range(num_credits):
        credit = {
            'id': generate_uuid(),
            'account_id': random.choice(account_ids),
            'credit_limit': generate_amount(),
            'interest_rate': round(random.uniform(0.01, 29.99), 2),
            'due_date': (datetime.now() + timedelta(days=random.randint(1, 30))).strftime('%Y-%m-%d'),
            'minimum_payment': generate_amount(),
            'created_at': generate_timestamp(),
            'updated_at': generate_timestamp(),
            'last_synced_at': generate_timestamp()
        }
        credits.append(credit)
    return credits


def generate_account_depositories(num_depositories, account_ids):
    depositories = []
    for _ in range(num_depositories):
        depository = {
            'id': generate_uuid(),
            'account_id': random.choice(account_ids),
            'bank_name': ''.join(random.choice(string.ascii_lowercase) for _ in range(10)),
            'account_number': ''.join(random.choice(string.digits) for _ in range(10)),
            'routing_number': ''.join(random.choice(string.digits) for _ in range(9)),
            'interest_rate': round(random.uniform(0.01, 5.00), 2),
            'overdraft_limit': generate_amount(),
            'created_at': generate_timestamp(),
            'updated_at': generate_timestamp(),
            'last_synced_at': generate_timestamp()
        }
        depositories.append(depository)
    return depositories


def generate_account_investments(num_investments, account_ids):
    investments = []
    for _ in range(num_investments):
        investment = {
            'id': generate_uuid(),
            'account_id': random.choice(account_ids),
            'investment_type': random.choice(['stocks', 'bonds', 'real estate']),
            'market_value': generate_amount(),
            'purchase_price': generate_amount(),
            'purchase_date': (datetime.now() - timedelta(days=random.randint(1, 1000))).strftime('%Y-%m-%d'),
            'dividends': generate_amount(),
            'interest_rate': round(random.uniform(0.01, 5.00), 2),
            'created_at': generate_timestamp(),
            'updated_at': generate_timestamp(),
            'last_synced_at': generate_timestamp()
        }
        investments.append(investment)
    return investments


def generate_account_loans(num_loans, account_ids):
    loans = []
    for _ in range(num_loans):
        loan = {
            'id': generate_uuid(),
            'account_id': random.choice(account_ids),
            'loan_amount': generate_amount(),
            'outstanding_balance': generate_amount(),
            'interest_rate': round(random.uniform(0.01, 29.99), 2),
            'loan_term': random.choice(['15 years', '30 years']),
            'due_date': (datetime.now() + timedelta(days=random.randint(1, 30))).strftime('%Y-%m-%d'),
            'payment_frequency': random.choice(['monthly', 'bi-weekly']),
            'collateral': ''.join(random.choice(string.ascii_lowercase) for _ in range(10)),
            'created_at': generate_timestamp(),
            'updated_at': generate_timestamp(),
            'last_synced_at': generate_timestamp()
        }
        loans.append(loan)
    return loans


def generate_account_other_assets(num_assets, account_ids):
    assets = []
    for _ in range(num_assets):
        asset = {
            'id': generate_uuid(),
            'account_id': random.choice(account_ids),
            'asset_type': random.choice(['real estate', 'vehicle']),
            'purchase_price': generate_amount(),
            'current_value': generate_amount(),
            'purchase_date': (datetime.now() - timedelta(days=random.randint(1, 1000))).strftime('%Y-%m-%d'),
            'depreciation_rate': round(random.uniform(0.01, 10.00), 2),
            'created_at': generate_timestamp(),
            'updated_at': generate_timestamp(),
            'last_synced_at': generate_timestamp()
        }
        assets.append(asset)
    return assets


def main():
    num_users = 10
    num_accounts = 100
    num_categories = 20
    num_transactions = 200
    num_budgets = 8
    num_recurring_transactions = 5
    num_reports = 10
    num_notifications = 15
    num_balances = 50
    num_credits = 20
    num_depositories = 20
    num_investments = 20
    num_loans = 20
    num_other_assets = 20

    users = generate_users(num_users)
    user_ids = [user['user_id'] for user in users]
    accounts = generate_accounts(num_accounts, user_ids)
    account_ids = [account['account_id'] for account in accounts]
    categories = generate_categories(num_categories, user_ids)
    category_ids = [category['category_id'] for category in categories]
    transactions = generate_transactions(num_transactions, account_ids, category_ids)
    budgets = generate_budgets(num_budgets, user_ids, category_ids)
    recurring_transactions = generate_recurring_transactions(num_recurring_transactions, user_ids, category_ids)
    reports = generate_reports(num_reports, user_ids)
    notifications = generate_notifications(num_notifications, user_ids)
    balances = generate_account_balances(num_balances, account_ids)
    credits = generate_account_credits(num_credits, account_ids)
    depositories = generate_account_depositories(num_depositories, account_ids)
    investments = generate_account_investments(num_investments, account_ids)
    loans = generate_account_loans(num_loans, account_ids)
    other_assets = generate_account_other_assets(num_other_assets, account_ids)

    # Print SQL statements
    for user in users:
        print(generate_sql_insert('users', user))

    for account in accounts:
        print(generate_sql_insert('accounts', account))

    for category in categories:
        print(generate_sql_insert('categories', category))

    for transaction in transactions:
        print(generate_sql_insert('transactions', transaction))

    for budget in budgets:
        print(generate_sql_insert('budgets', budget))

    for recurring_transaction in recurring_transactions:
        print(generate_sql_insert('recurring_transactions', recurring_transaction))

    for report in reports:
        print(generate_sql_insert('reports', report))

    for notification in notifications:
        print(generate_sql_insert('notifications', notification))

    for balance in balances:
        print(generate_sql_insert('account_balances', balance))

    for credit in credits:
        print(generate_sql_insert('account_credits', credit))

    for depository in depositories:
        print(generate_sql_insert('account_depositories', depository))

    for investment in investments:
        print(generate_sql_insert('account_investments', investment))

    for loan in loans:
        print(generate_sql_insert('account_loans', loan))

    for asset in other_assets:
        print(generate_sql_insert('account_other_assets', asset))


if __name__ == '__main__':
    main()
