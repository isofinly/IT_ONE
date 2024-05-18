import random

currencies = ['USD', 'EUR', 'GBP', 'JPY', 'AUD', 'CAD']
currencies_rev = list(reversed(currencies))

for currency in currencies:
    print(f'{currency}_{currencies_rev[currencies_rev.index(currency) - 1]},{random.randint(100, 1000)/random.randint(1, 10):.2f}')