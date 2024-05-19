import csv
import random
import string
from datetime import datetime, timedelta

def generate_transaction_id():
    prefixes = ['TRX', 'PAY', 'INV', 'EXP']
    return random.choice(prefixes) + ''.join(random.choice(string.ascii_uppercase + string.digits) for _ in range(6))

def generate_bank_name():
    adjectives = ['First', 'National', 'Global', 'International', 'Pacific', 'Sunrise']
    nouns = ['Bank', 'Finance', 'Trust', 'Capital', 'Group']
    return random.choice(adjectives) + ' ' + random.choice(nouns)

def generate_account_number():
    return ''.join(str(random.randint(0, 9)) for _ in range(12))

def generate_currency():
    currencies = ['USD', 'EUR', 'GBP', 'JPY', 'AUD', 'CAD', 'CHF', 'CNY', 'INR']
    return random.choice(currencies)

def generate_sample_data(num_rows):
    with open('transactions.csv', 'w', newline='') as csvfile:
        fieldnames = ['transaction_id', 'bank_name', 'account_number', 'transaction_date', 'amount', 'currency', 'transaction_type']
        writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
        writer.writeheader()
        
        for _ in range(num_rows):
            transaction_id = generate_transaction_id()
            bank_name = generate_bank_name()
            account_number = generate_account_number()
            transaction_date = datetime.now() - timedelta(days=random.randint(1, 365))
            amount = round(random.uniform(10, 1000), 2)
            currency = generate_currency()
            transaction_type = random.choice(['debit', 'credit', 'transfer', 'withdrawal', 'deposit', 'refund'])
            
            writer.writerow({
                'transaction_id': transaction_id,
                'bank_name': bank_name,
                'account_number': account_number,
                'transaction_date': transaction_date.strftime('%Y-%m-%d %H:%M:%S'),
                'amount': amount,
                'currency': currency,
                'transaction_type': transaction_type
            })

# Generate 100 rows of sample data
generate_sample_data(100)
