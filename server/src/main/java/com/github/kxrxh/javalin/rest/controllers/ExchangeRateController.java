import java.sql.Date;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExchangeRateController {

    private ExchangeRateController() {
    }

    public static void createExchangeRate(Context ctx) {
        try {
            String baseCurrency = ctx.formParam("base_currency");
            String convertedCurrency = ctx.formParam("converted_currency");
            String rateStr = ctx.formParam("rate");
            String dateStr = ctx.formParam("date");

            if (baseCurrency == null || convertedCurrency == null || rateStr == null || dateStr == null) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            double rate = Double.parseDouble(rateStr);
            Date date = Date.valueOf(dateStr);

            ExchangeRateService.createExchangeRate(baseCurrency, convertedCurrency, rate, date);
            ctx.status(200).result("Exchange rate created successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void readExchangeRate(Context ctx) {
        try {
            String baseCurrency = ctx.queryParam("base_currency");
            String convertedCurrency = ctx.queryParam("converted_currency");
            String dateStr = ctx.queryParam("date");

            if (baseCurrency == null || convertedCurrency == null || dateStr == null) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            Date date = Date.valueOf(dateStr);
            ExchangeRate exchangeRate = ExchangeRateService.readExchangeRate(baseCurrency, convertedCurrency, date);
            ctx.json(exchangeRate);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void updateExchangeRate(Context ctx) {
        try {
            String idStr = ctx.pathParam("id");
            String baseCurrency = ctx.formParam("base_currency");
            String convertedCurrency