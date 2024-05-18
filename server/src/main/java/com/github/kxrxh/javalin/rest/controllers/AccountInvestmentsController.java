@Slf4j
public class AccountInvestmentsController {

    private AccountInvestmentsController() {
    }

    public static void createInvestment(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            String accountIdStr = ctx.formParam("account_id");
            String investmentType = ctx.formParam("investment_type");
            String marketValueStr = ctx.formParam("market_value");
            String purchasePriceStr = ctx.formParam("purchase_price");
            String purchaseDateStr = ctx.formParam("purchase_date");
            String dividendsStr = ctx.formParam("dividends");
            String interestRateStr = ctx.formParam("interest_rate");

            if (accountIdStr == null || marketValueStr == null || purchasePriceStr == null || purchaseDateStr == null || dividendsStr == null || interestRateStr == null) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID accountId = UUID.fromString(accountIdStr);
            long marketValue = Long.parseLong(marketValueStr);
            long purchasePrice = Long.parseLong(purchasePriceStr);
            LocalDate purchaseDate = LocalDate.parse(purchaseDateStr);
            long dividends = Long.parseLong(dividendsStr);
            double interestRate = Double.parseDouble(interestRateStr);

            AccountInvestmentsService.createInvestment(userId, accountId, investmentType, marketValue, purchasePrice, purchaseDate, dividends, interestRate);
            ctx.status(200).result("Investment created successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void readInvestment(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            String investmentIdStr = ctx.pathParam("investment_id");

            if (investmentIdStr == null) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID investmentId = UUID.fromString(investmentIdStr);
            AccountInvestment investment = AccountInvestmentsService.readInvestment(userId, investmentId);
            ctx.json(investment);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void updateInvestment(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            String investmentIdStr = ctx.pathParam("investment_id");
            String accountIdStr = ctx.formParam("account_id");
            String investmentType = ctx.formParam("investment_type");
            String marketValueStr = ctx.formParam("market_value");
            String purchasePriceStr = ctx.formParam("purchase_price");
            String purchaseDateStr = ctx.formParam("purchase_date");
            String dividendsStr = ctx.formParam("dividends");
            String interestRateStr = ctx.formParam("interest_rate");

            if (investmentIdStr == null || accountIdStr == null || marketValueStr == null || purchasePriceStr == null || purchaseDateStr == null || dividendsStr == null || interestRateStr == null) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID investmentId = UUID.fromString(investmentIdStr);
            UUID accountId = UUID.fromString(accountIdStr);
            long marketValue = Long.parseLong(marketValueStr);
            long purchasePrice = Long.parseLong(purchasePriceStr);
            LocalDate purchaseDate = LocalDate.parse(purchaseDateStr);
            long dividends = Long.parseLong(dividendsStr);
            double interestRate = Double.parseDouble(interestRateStr);

            AccountInvestmentsService.updateInvestment(userId, investmentId, accountId, investmentType, marketValue, purchasePrice, purchaseDate, dividends, interestRate);
            ctx.status(200).result("Investment updated successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void deleteInvestment(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            String investmentIdStr = ctx.pathParam("investment_id");

            if (investmentIdStr == null) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID investmentId = UUID.fromString(investmentIdStr);
            AccountInvestmentsService.deleteInvestment(userId, investmentId);
            ctx.status(200).result("Investment deleted successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void calculateDividends(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            String investmentIdStr = ctx.pathParam("investment_id");

            if (investmentIdStr == null) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID investmentId = UUID.fromString(investmentIdStr);
            AccountInvestmentsService.calculateDividends(userId, investmentId);
            ctx.status(200).result("Dividends calculated and updated successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }
}
