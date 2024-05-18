1. Восстановление пароля (Password Recovery)
    * POST /api/v1/users/recover
    * Описание: Инициирование процесса восстановления пароля.
    * Параметры: email.
2. Смена пароля (Change Password)
    * PUT /api/v1/users/{user_id}/password
    * Описание: Смена пароля пользователя.
    * Параметры: old_password, new_password.
      Расширенные возможности счетов (Accounts)
3. Could not merge accounts: ERROR: new row for relation "accounts" violates check constraint "accounts_account_type_check"
  Detail: Failing row contains (b77a9a71-fc87-4815-b964-01c3cf9f5c15, f433bdf4-3194-41e3-b08d-8fdb20774849, null, TEST, null, TESTYOMH, 2024-05-18 13:56:18.592667, 2024-05-18 13:56:18.592667, null, null, 9, RUB, t, ok, [], [], null).