# Описание проекта для IT-ONE

## Конфигурация NATS.io

Command to add local message: `nats context add localhost --description "Localhost"`

Command to listen for changes in message queue: `nats sub db_sync`

## Интеграция со сторонними банками

Для эмуляции сторонних сервисов был использован `roapi`.
Формат данных:
| transaction_id | bank_name | account_number | transaction_date | amount | currency | transaction_type |
|----------------|-----------|----------------|------------------|--------|----------|------------------|
| INVN8FNSO | Global Bank | 923139644042 | 2023-10-31 20:29:43 | 295.21 | INR | deposit |

## Получение данных с localhost:8080 (развертывание в докере)

## Примеры запросов

* Using SQL

```bash
curl -X POST -d "SELECT transaction_id, bank_name, account_number, transaction_date, amount, currency, transaction_type FROM transactions LIMIT 2" localhost:8080/api/sql
```

* Using GraphQL

```bash
curl -X POST -d "query { transactions(limit: 2) {transaction_id, bank_name, account_number, transaction_date, amount, currency, transaction_type} }" localhost:8080/api/graphql
```

* Using REST API

```bash
curl "localhost:8080/api/tables/transactions?columns=transaction_id,bank_name,account_number,transaction_date,amount,currency,transaction_type&limit=2"
```
