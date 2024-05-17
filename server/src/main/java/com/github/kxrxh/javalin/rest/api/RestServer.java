package com.github.kxrxh.javalin.rest.api;

import com.github.kxrxh.javalin.rest.controllers.*;
import io.javalin.Javalin;
import lombok.extern.slf4j.Slf4j;

/**
 * Represents a RESTful server built with Javalin framework.
 */
@Slf4j
public class RestServer {
  private final Javalin app;

  /**
   * Constructs a new RestServer.
   */
  public RestServer(boolean dev) {
    this.app = Javalin.create(config -> {
      if (dev) {
        config.bundledPlugins.enableDevLogging();
      }
      config.bundledPlugins.enableCors(cors -> cors.addRule(it -> it.allowHost("localhost")));
    });
    app.after(ctx -> log.info(ctx.req().getMethod() + " " + ctx.req().getPathInfo() + " " + ctx.statusCode()));
  }

  public RestServer() {
    this(false);
  }

  /**
   * Sets up open routes for the RESTful API.
   */
  public void setupRoutes() {
    app.get("/", ctx -> ctx.result("Hello World!"));

    // Account routes
    /*
     * Перевод средств между счетами (Transfer Funds)
     * POST /api/v1/accounts/transfer
     * Описание: Перевод средств между счетами пользователя.
     * Параметры: from_account_id, to_account_id, amount.
     */
    app.post("/api/v1/accounts/transfer", AccountController::transferFunds);

    /*
     * Объединение нескольких счетов (Merge Accounts)
     * POST /api/v1/accounts/merge
     * Описание: Объединение нескольких счетов в один.
     * Параметры: account_ids[], new_account_name.
     * Расширенные возможности транзакций (Transactions)
     */
    app.post("/api/v1/accounts/merge", AccountController::mergeAccounts);

    // Transaction routes
    /*
     * Поиск транзакций (Search Transactions)
     * GET /api/v1/transactions/search
     * Описание: Поиск транзакций по различным параметрам.
     * Параметры: user_id, amount_range, date_range, category_id, description.
     */
    app.get("/api/v1/transactions/search", TransactionController::searchTransactions);

    /*
     * Периодические транзакции (Recurring Transactions)
     * POST /api/v1/transactions/recurring
     * Описание: Создание повторяющихся транзакций.
     * Параметры: user_id, amount, category_id, description, frequency.
     * Расширенные возможности категорий (Categories)
     */
    app.post("/api/v1/transactions/recurring", TransactionController::createRecurringTransaction);

    // Category routes
    /*
     * Анализ категории (Category Analysis)
     * GET /api/v1/categories/{category_id}/analysis
     * Описание: Получение анализа расходов по категории.
     * Параметры: category_id, date_range.
     * Расширенные возможности бюджетов (Budgets)
     */
    app.get("/api/v1/categories/{category_id}/analysis", CategoryController::analyzeCategory);

    // Budget routes
    /*
     * Предупреждения по бюджету (Budget Alerts)
     * POST /api/v1/budgets/{budget_id}/alerts
     * Описание: Настройка предупреждений при достижении определенных лимитов.
     * Параметры: budget_id, alert_threshold.
     */
    app.post("/api/v1/budgets/{budget_id}/alerts", BudgetController::setBudgetAlert);

    /*
     * Анализ бюджета (Budget Analysis)
     * GET /api/v1/budgets/{budget_id}/analysis
     * Описание: Получение анализа эффективности бюджета.
     * Параметры: budget_id.
     * Отчеты и визуализация (Reports and Visualization)
     */
    app.get("/api/v1/budgets/{budget_id}/analysis", BudgetController::analyzeBudget);

    // Report routes
    /*
     * Создание отчетов (Generate Reports)
     * POST /api/v1/reports
     * Описание: Создание финансовых отчетов за выбранный период.
     * Параметры: year, month.
     * curl -X POST -d '{year: 2022, month: 1}' http://localhost:8080/api/v1/reports
     */
    app.get("/api/v1/reports", ReportController::generateMonthlyReport);

    /*
     * Получение отчетов (Get Reports)
     * GET /api/v1/reports/{report_id}
     * Описание: Получение сгенерированного финансового отчета.
     * Параметры: report_id.
     */
    app.get("/api/v1/reports/{report_id}", ReportController::getReport);

    // Visualization routes
    /*
     * TODO complete
     * Визуализация данных (Data Visualization)
     * GET /api/v1/visualizations
     * Описание: Получение визуализации финансовых данных пользователя.
     * Параметры: user_id, visualization_type, date_range.
     * Финансовые советы и прогнозы (Financial Advice and Forecasting)
     */
    app.get("/api/v1/visualizations", VisualizationController::getVisualization);

    // Advice routes
    /*
     * Финансовые советы (Financial Advice)
     * GET /api/v1/advice
     * Описание: Получение персонализированных финансовых советов.
     * Параметры: user_id.
     */
    app.get("/api/v1/advice", AdviceController::getFinancialAdvice);

    /*
     * Прогнозирование финансов (Financial Forecasting)
     * GET /api/v1/forecast
     * Описание: Прогнозирование финансового состояния пользователя на основе
     * текущих данных.
     * Параметры: user_id, date_range.
     * Интеграции и автоматизация (Integrations and Automation)
     */
    app.get("/api/v1/forecast", AdviceController::getFinancialForecast);

    // Integration routes
    /*
     * TODO Интеграция с банками (Bank Integrations)
     * POST /api/v1/integrations/banks
     * Описание: Интеграция с банковскими аккаунтами для автоматического получения
     * транзакций.
     * Параметры: user_id, bank_credentials.
     */
    app.post("/api/v1/integrations/banks", IntegrationController::integrateWithBank);
    /*
     * Автоматическое категорирование транзакций (Automatic Transaction
     * Categorization)
     * POST /api/v1/transactions/auto-categorize
     * Описание: Автоматическое категорирование транзакций на основе правил и
     * истории.
     * Параметры: user_id.
     */
    app.post("/api/v1/transactions/auto-categorize", IntegrationController::autoCategorizeTransactions);

    // Notification routes
    /*
     * Уведомления (Notifications)
     * POST /api/v1/notifications
     * Описание: Настройка уведомлений для различных событий (например, достижение
     * бюджетного лимита, поступление зарплаты).
     * Параметры: user_id, notification_type, threshold.
     */
    app.post("/api/v1/notifications", NotificationController::setNotification);
  }

  /**
   * Sets up JWT authentication for protected routes.
   *
   * @param protectedRoute The route prefix for protected routes.
   * @param jwtSecret      The secret key used for JWT token generation and
   *                       validation.
   */
  public void setupJWTAuthentication(String protectedRoute, String jwtSecret) {
    AuthController.initialize("my-key");
    app.before(protectedRoute + "/*", AuthController::securityRouteHandler);
    app.post(protectedRoute + "/login", AuthController::loginRouteHandler);
    app.post(protectedRoute + "/register", AuthController::registerRouteHandler);
  }

  /**
   * Starts the server and listens on the specified port.
   *
   * @param port The port number on which the server will listen.
   */
  public void listen(int port) {
    app.start(port);
  }
}
