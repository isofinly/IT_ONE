1. Восстановление пароля (Password Recovery)
    * POST /api/v1/users/recover
    * Описание: Инициирование процесса восстановления пароля.
    * Параметры: email.
2. Смена пароля (Change Password)
    * PUT /api/v1/users/{user_id}/password
    * Описание: Смена пароля пользователя.
    * Параметры: old_password, new_password.
      Расширенные возможности счетов (Accounts)
