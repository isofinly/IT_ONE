
# Описание проекта для IT-ONE
## Реализация

|                 |                                                                                                       |
|-----------------|-------------------------------------------------------------------------------------------------------|
| Архитектура     | микросервисы развернутые в docker                                                                     |
| Оформление кода | Линт sonarqube и intellij                                                                             |
| DevOps          | CI/CD Pipeline. Создание докер-образа. Grafana + Prometheus                                           |
| Тестирование    | Smoke, Load, jUnit                                                                                    |
| Безопасность    | SSL сертификаты на все соединения, которые его поддерживают.  SSL для интеграции с внешними сервисами |

## Требования и описание продукта

2.2 Общее описание

2.2.1 Функции продукта
- Учет расходов - позволяет пользователям вводить свои ежедневные расходы по категориям
- Создание бюджетов - пользователи могут устанавливать бюджетные ограничения по категориям расходов на месяц/неделю/год
- Учет налогов, инвестиций, депозитов, займов и кредитов 
- Генерация отчетов и аналитика - создание отчётов для определенного промежутка времени
- Напоминания - приложение может отправлять напоминания при приближении к бюджетным лимитам

2.2.2 Характеристики пользователей
- Целевая аудитория - физические лица, желающие лучше контролировать свои финансы
- Пользователи разного возраста и уровня дохода, владеющие навыками ПК.
- Разный уровень технических знаний

2.2.3 Влияющие факторы и зависимости 
- Использование на мобильных платформах (iOS, Android) и веб-версия
- Возможность интеграции с банковскими приложениями для импорта данных (опционально)
- Общее финансовое положение людей в стране

2.2.4 Ограничения
- Невысокая производительность языка реализации
- Производительность - быстрый отклик, плавная работа
- Защита персональных данных пользователей  

2.3 Спецификация требований

2.3.1 Функицональные требования
   - Должен быть учет расходов и доходов
   - Должна быть возможность добавлять новые расходы вручную
   - Должна быть возможность создавать категории, суб-аккаунты, счета и вводить транзации  
   - Должна быть категоризация расходов по типам (продукты, транспорт, развлечения и др.)  
   - Должен быть ввод описания, суммы, валюты, даты расхода и дальнейший анализ по уникальному идентификатору пользователя
   - Должна быть возможность ставить лимиты на бюджеты
   - Должна быть возможность расчета процентной ставки по долгам и кредитам, как и расчет максимального лимита займа 
   - Должно быть создание нескольких бюджетов на разные периоды - месячных, годовых и т.д.
   - Должно быть назначение бюджетных лимитов по категориям расходов  
   - Должна быть персонализированная аналитика и отчеты
   - Сравнение реальных трат с запланированным бюджетом
   - Учетные записи для нескольких членов семьи
   - Общий семейный бюджет или индивидуальные
 
2.3.2 Требования к надежности
- Система должна обеспечивать 98% доступность сервиса в течение года.
- Должно быть настроено резервное копирование данных
- Платформа должна поддерживать корректно проводить транзакции при максимально допустимой нагрузке
- Платформа должна быть отказоустойчива для 100 человек одновременно и для 50 транзакций в секунду
- Система должна иметь механизмы защиты от внешних атак 
- Система должна использовать только защищенные соединения

## Быстрый старт

* **Для начала необходимо сгенерировать SSL-сертификаты и ключи, выполнив следующую команду:**

```sh
./key_generation.sh
```

* **После этого вам останется только запустить *'docker-compose up'*:**

```sh
docker-compose up
```

### Документация к API

#### [Documentation](/server/README.md)

### Развернутое приложение для тестирования endpoint'ов
#### [Ссылка](https://dashboard.render.com/)
 **Не все пути могут работать и сразу дать ответ из-за особенностей бесплатного хостинга** `render` 
 
 Для надлежащего ознакомления с проектом, скачивайте/клонируйте и запускайте локально/на хорошем сервере. 

## Краткая информация

### Защищенное соединение

В проекте мы придаем особое внимание безопасности передачи данных, используя SSL-шифрование для всех обращений к базе данных, NATS.io и внешним сервисам. Это позволяет обеспечить конфиденциальность и целостность информации, передаваемой между различными компонентами системы.

Кроме того, NATS.io может также выступать в роли "посредника" между сервисами, обеспечивая защищенное соединение между ними. Это означает, что даже если сеть подвергнется атаке или перехвату данных, информация будет защищена благодаря использованию SSL-шифрования на уровне NATS.io.

### Внешние сервисы

Для эмуляции внешного сервиса (API Банка) мы используем **roapi**. **roapi** предоставляет нам внешнюю REST-API, которое завязано на данных из Google.

### Средства мониторинга

Для мониторинга работы нашего приложения мы использовали *Prometheus* + *Grafana*, которые разворачиваются вместе с приложением.

Мониторинг находится на следующих адресах:

#### Prometheus

* <http://localhost:7080/>
* <http://localhost:9090/>

#### Grafana

Логин и пароль: *admin*

* <http://localhost:3000/>

## Тестирование

Мы реализовали 2 типа тестов:

* JUnit:
  * Юнит тесты для сущностей и компонентов бизнес логики
* Grafana k6:
  * Нагрузочное тестирование базовое и продвинутое

### Отчёт по нагрузочному тестированию (Advanced)

#### Сценарий нагрузочного теста

* 1 сценарий
* Максимум 50 одновременных виртуальных пользователей (VUs)  
* Общая продолжительность 2 минуты 30 секунд, включая плавный завершающий этап

#### Метрики

* Проверки: 100% успешных (3683 из 3683)
* Ошибки: 0%  
* Данные получено: 1.9 МБ (13 кБ/с)
* Данные отправлено: 1.3 МБ (8.6 кБ/с)

#### Время отклика HTTP

* Среднее: 87.42 мс
* Медиана: 42.27 мс
* 90-й перцентиль: 241.77 мс
* 95-й перцентиль: 246.12 мс
* Минимум: 135.51 мкс
* Максимум: 496.57 мс  

#### Задержки времени отклика

* Блокировка: средн. 3.19 мкс, макс. 193.38 мкс  
* Подключение: средн. 792 нс, макс. 73.95 мкс
* Получение данных: средн. 25.94 мкс, макс. 192.93 мкс
* Отправка данных: средн. 10.09 мкс, макс. 196.93 мкс
* Ожидание: средн. 87.39 мс, макс. 496.54 мс

#### Скорость запросов

* 24.55 запросов в секунду

#### Итерации

* 0 завершенных
* 50 прерванных после 2 минут

#### Загрузка VUs

* 50 максимальное количество
* Средняя загрузка 50 VUs

#### Основные выводы

* Никаких ошибок не было обнаружено
* Медианное время отклика 42 мс является приемлемым
* 90-й и 95-й перцентили времени отклика относительно высоки (241 мс и 246 мс соответственно)
* Максимальное время отклика 496 мс может быть неприемлемым для некоторых критических операций
* Скорость запросов 24.55 в секунду возможно низковата для высоконагруженной системы

[Результаты тестирования (Advanced)](/k6/advanced-out.md)

[Результаты тестирования (Basic)](/k6/basic-out.md)

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
