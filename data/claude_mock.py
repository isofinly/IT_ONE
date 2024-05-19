import uuid
import random
import string
from datetime import datetime, timedelta, date

# Helper functions


def random_string(length):
    letters = string.ascii_letters + string.digits
    return ''.join(random.choice(letters) for _ in range(length))


def random_date(start, end):
    return start + timedelta(seconds=random.randint(0, int((end - start).total_seconds())))


def generate_timestamp():
    return datetime.now().strftime('%Y-%m-%d %H:%M:%S')


def random_currency():
    currencies = ['USD', 'EUR', 'GBP', 'JPY', 'AUD', 'CAD']
    return random.choice(currencies)


def random_account_type():
    account_types = ['Checking', 'Savings', 'Investment',
                     'Credit', 'Credit Card', 'Debt', 'Asset']
    return random.choice(account_types)


def random_account_status():
    account_statuses = ['ok', 'syncing', 'error']
    return random.choice(account_statuses)


def random_import_status():
    import_statuses = ['pending', 'importing', 'complete', 'failed']
    return random.choice(import_statuses)

# Data generation functions


def generate_families(num_families):
    families = []
    for _ in range(num_families):
        family = {
            'family_id': str(uuid.uuid4()),
            'name': random_string(10),
            'currency': random_currency()
        }
        families.append(family)
    return families


def generate_users(families, num_users_per_family):
    users = []
    for family in families:
        for _ in range(num_users_per_family):
            user = {
                'user_id': str(uuid.uuid4()),
                'family_id': family['family_id'],
                'first_name': random_string(8),
                'last_name': random_string(10),
                'email': f"{random_string(6)}@example.com",
                'password_digest': random_string(20),
                'active': random.choice([True, False])
            }
            users.append(user)
    return users


def generate_accounts(users, families, num_accounts_per_user):
    accounts = []
    for user in users:
        for _ in range(num_accounts_per_user):
            account = {
                'account_id': str(uuid.uuid4()),
                'user_id': user['user_id'],
                'family_id': user['family_id'],
                'account_type': random_account_type(),
                'subtype': random_string(10),
                'account_name': random_string(15),
                'accountable_type': random_string(10),
                'accountable_id': str(uuid.uuid4()),
                'balance': random.randint(1000, 100000),
                'currency': random_currency(),
                'is_active': random.choice([True, False]),
                'status': random_account_status(),
            }
            accounts.append(account)
    return accounts


def generate_account_credits(accounts, num_credits_per_account):
    account_credits = []
    for account in accounts:
        for _ in range(num_credits_per_account):
            account_credit = {
                'id': str(uuid.uuid4()),
                'account_id': account['account_id'],
                'credit_limit': random.randint(1000, 100000),
                'interest_rate': round(random.uniform(0.05, 0.25), 2),
                'minimum_payment': random.randint(100, 1000)
            }
            account_credits.append(account_credit)
    return account_credits


def generate_account_depositories(accounts, num_depositories_per_account):
    account_depositories = []
    for account in accounts:
        for _ in range(num_depositories_per_account):
            account_depository = {
                'id': str(uuid.uuid4()),
                'account_id': account['account_id'],
                'bank_name': random_string(10),
                'account_number': random_string(12),
                'routing_number': random_string(9),
                'interest_rate': round(random.uniform(0.01, 0.05), 2),
                'overdraft_limit': random.randint(1000, 10000)
            }
            account_depositories.append(account_depository)
    return account_depositories


def generate_account_investments(accounts, num_investments_per_account):
    account_investments = []
    for account in accounts:
        for _ in range(num_investments_per_account):
            account_investment = {
                'id': str(uuid.uuid4()),
                'account_id': account['account_id'],
                'investment_type': random_string(10),
                'market_value': random.randint(10000, 500000),
                'purchase_price': random.randint(5000, 200000),
                'purchase_date': random_date(date(2020, 1, 1), date(2023, 1, 1)),
                'dividends': random.randint(100, 5000),
                'interest_rate': round(random.uniform(0.02, 0.08), 2)
            }
            account_investments.append(account_investment)
    return account_investments


def generate_account_loans(accounts, num_loans_per_account):
    account_loans = []
    for account in accounts:
        for _ in range(num_loans_per_account):
            account_loan = {
                'id': str(uuid.uuid4()),
                'account_id': account['account_id'],
                'loan_amount': random.randint(10000, 500000),
                'outstanding_balance': random.randint(5000, 200000),
                'interest_rate': round(random.uniform(0.03, 0.12), 2),
                'loan_term': random_string(5),
                'due_date': random_date(date(2023, 1, 1), date(2030, 1, 1)),
                'payment_frequency': random_string(7),
                'collateral': random_string(10)
            }
            account_loans.append(account_loan)
    return account_loans


def generate_account_other_assets(accounts, num_assets_per_account):
    account_other_assets = []
    for account in accounts:
        for _ in range(num_assets_per_account):
            account_other_asset = {
                'id': str(uuid.uuid4()),
                'account_id': account['account_id'],
                'asset_type': random_string(10),
                'purchase_price': random.randint(10000, 500000),
                'current_value': random.randint(5000, 600000),
                'purchase_date': random_date(date(2015, 1, 1), date(2023, 1, 1)),
                'depreciation_rate': round(random.uniform(0.02, 0.1), 2)
            }
            account_other_assets.append(account_other_asset)
    return account_other_assets


def generate_account_balances(accounts, num_balances_per_account):
    account_balances = []
    for account in accounts:
        for _ in range(num_balances_per_account):
            account_balance = {
                'id': str(uuid.uuid4()),
                'account_id': account['account_id'],
                'date': random_date(date(2022, 1, 1), date(2023, 1, 1)),
                'balance': random.randint(1000, 100000),
                'currency': account['currency']
            }
            account_balances.append(account_balance)
    return account_balances


def generate_categories(families, num_categories_per_family):
    categories = []
    for family in families:
        for _ in range(num_categories_per_family):
            category = {
                'category_id': str(uuid.uuid4()),
                'name': random_string(10),
                'family_id': family['family_id']
            }
            categories.append(category)
    return categories


def generate_transactions(accounts, categories, num_transactions_per_account):
    transactions = []
    for account in accounts:
        for _ in range(num_transactions_per_account):
            transaction = {
                'transaction_id': str(uuid.uuid4()),
                'name': random_string(15),
                'date': generate_timestamp(),
                'amount': random.randint(10, 10000),
                'transaction_type': random.choice(['inflow', 'outflow']),
                'currency': account['currency'],
                'account_id': account['account_id'],
                'category_id': random.choice(categories)['category_id'] if categories else None,
                'excluded': random.choice([True, False]),
                'notes': random_string(20)
            }
            transactions.append(transaction)
    return transactions


def generate_budgets(users, categories, num_budgets_per_user):
    budgets = []
    for user in users:
        for _ in range(num_budgets_per_user):
            budget = {
                'budget_id': str(uuid.uuid4()),
                'user_id': user['user_id'],
                'category_id': random.choice(categories)['category_id'] if categories else None,
                'limit_amount': random.randint(1000, 10000),
                'start_date': generate_timestamp(),
                'end_date': generate_timestamp(),
                'alert_threshold': random.randint(100, 1000)
            }
            budgets.append(budget)
    return budgets


def generate_recurring_transactions(users, categories, num_recurring_transactions_per_user):
    recurring_transactions = []
    for user in users:
        for _ in range(num_recurring_transactions_per_user):
            recurring_transaction = {
                'recurring_transaction_id': str(uuid.uuid4()),
                'user_id': user['user_id'],
                'amount': random.randint(100, 5000),
                'category_id': random.choice(categories)['category_id'] if categories else None,
                'description': random_string(20),
                'frequency': random.randint(7, 90)  # Frequency in days
            }
            recurring_transactions.append(recurring_transaction)
    return recurring_transactions


def generate_reports(users, num_reports_per_user):
    reports = []
    for user in users:
        for _ in range(num_reports_per_user):
            report = {
                'report_id': str(uuid.uuid4()),
                'user_id': user['user_id'],
                'report_type': random_string(10),
                'date_range': f"{random_date(date(2022, 1, 1), date(2023, 1, 1))} - {random_date(date(2023, 1, 1), date(2024, 1, 1))}"
            }
            reports.append(report)
    return reports


def generate_notifications(users, num_notifications_per_user):
    notifications = []
    for user in users:
        for _ in range(num_notifications_per_user):
            notification = {
                'notification_id': str(uuid.uuid4()),
                'user_id': user['user_id'],
                'notification_type': random_string(10),
                'threshold': random.randint(100, 5000)
            }
            notifications.append(notification)
    return notifications


def generate_exchange_rates(num_exchange_rates):
    exchange_rates = []
    for _ in range(num_exchange_rates):
        exchange_rate = {
            'id': str(uuid.uuid4()),
            'base_currency': random_currency(),
            'converted_currency': random_currency(),
            'rate': random.randint(10, 1000),
            'date': random_date(date(2022, 1, 1), date(2023, 1, 1))
        }
        exchange_rates.append(exchange_rate)
    return exchange_rates


def generate_imports(accounts, num_imports_per_account):
    imports = []
    for account in accounts:
        for _ in range(num_imports_per_account):
            import_data = {
                'id': str(uuid.uuid4()),
                'account_id': account['account_id'],
                'column_mappings': {
                    'date': random_string(5),
                    'amount': random_string(6),
                    'description': random_string(7)
                },
                'status': random_import_status(),
                'raw_csv_str': random_string(100),
                'normalized_csv_str': random_string(80)
            }
            imports.append(import_data)
    return imports


def generate_valuations(accounts, num_valuations_per_account):
    valuations = []
    for account in accounts:
        for _ in range(num_valuations_per_account):
            valuation = {
                'id': str(uuid.uuid4()),
                'account_id': account['account_id'],
                'date': random_date(date(2022, 1, 1), date(2023, 1, 1)),
                'value': random.randint(10000, 500000),
                'currency': account['currency']
            }
            valuations.append(valuation)
    return valuations


def generate_taxes(num_taxes):
    taxes = []
    for _ in range(num_taxes):
        tax = {
            'id': str(uuid.uuid4()),
            'name': random_string(10),
            'description': random_string(20),
            'rate': random.randint(1, 50),
            'currency': random_currency()
        }
        taxes.append(tax)
    return taxes


# Example usage
num_families = 10
num_users_per_family = 3
num_accounts_per_user = 2
num_credits_per_account = 1
num_depositories_per_account = 1
num_investments_per_account = 1
num_loans_per_account = 1
num_assets_per_account = 1
num_balances_per_account = 5
num_categories_per_family = 5
num_transactions_per_account = 10
num_budgets_per_user = 3
num_recurring_transactions_per_user = 2
num_reports_per_user = 2
num_notifications_per_user = 2
num_exchange_rates = 10
num_imports_per_account = 2
num_valuations_per_account = 3
num_taxes = 10

families = generate_families(num_families)
users = generate_users(families, num_users_per_family)
accounts = generate_accounts(users, families, num_accounts_per_user)
account_credits = generate_account_credits(accounts, num_credits_per_account)
account_depositories = generate_account_depositories(
    accounts, num_depositories_per_account)
account_investments = generate_account_investments(
    accounts, num_investments_per_account)
account_loans = generate_account_loans(accounts, num_loans_per_account)
account_other_assets = generate_account_other_assets(
    accounts, num_assets_per_account)
account_balances = generate_account_balances(
    accounts, num_balances_per_account)
categories = generate_categories(families, num_categories_per_family)

transactions = generate_transactions(
    accounts, categories, num_transactions_per_account)
budgets = generate_budgets(accounts, categories, num_budgets_per_user)
recurring_transactions = generate_recurring_transactions(
    users, categories, num_recurring_transactions_per_user)
reports = generate_reports(users, num_reports_per_user)
notifications = generate_notifications(users, num_notifications_per_user)
exchange_rates = generate_exchange_rates(num_exchange_rates)
imports = generate_imports(accounts, num_imports_per_account)
valuations = generate_valuations(accounts, num_valuations_per_account)
taxes = generate_taxes(num_taxes)
balances = generate_account_balances(accounts, num_balances_per_account)
credits = generate_account_credits(accounts, num_credits_per_account)
depositories = generate_account_depositories(
    accounts, num_depositories_per_account)
investments = generate_account_investments(
    accounts, num_investments_per_account)
loans = generate_account_loans(accounts, num_loans_per_account)
other_assets = generate_account_other_assets(accounts, num_assets_per_account)


def generate_sql_insert(table_name, values):
    columns = ', '.join(values.keys())
    vs = []
    for value in values.values():
        if isinstance(value, str):
            value = f"'{value}'"
        elif isinstance(value, date):
            value = f"'{value.strftime('%Y-%m-%d %H:%M:%S')}'"
        else:
            value = str(value)
        vs.append(value)
    placeholders = ', '.join(vs)
    return f"INSERT INTO {table_name} ({columns}) VALUES ({placeholders});"


for family in families:
    print(generate_sql_insert('families', family))

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
    
for tax in taxes:
    print(generate_sql_insert('taxes', tax))

for valuation in valuations:
    print(generate_sql_insert('valuations', valuation))
    
# for _import in imports:
#     print(generate_sql_insert('imports', _import))
    
for exchange_rate in exchange_rates:
    print(generate_sql_insert('exchange_rates', exchange_rate))