package com.github.kxrxh.javalin.rest.controllers;
import com.github.kxrxh.javalin.rest.api.jwt.Utils;
import com.github.kxrxh.javalin.rest.database.models.AccountBalance;
import com.github.kxrxh.javalin.rest.database.models.AccountOtherAsset;
import com.github.kxrxh.javalin.rest.services.AccountBalancesService;
import com.github.kxrxh.javalin.rest.services.AccountOtherAssetsService;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
public class AccountOtherAssetsController {

    private AccountOtherAssetsController() {
    }

    public static void createAsset(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            String accountIdStr = ctx.formParam("account_id");
            String assetType = ctx.formParam("asset_type");
            String purchasePriceStr = ctx.formParam("purchase_price");
            String currentValueStr = ctx.formParam("current_value");
            String purchaseDateStr = ctx.formParam("purchase_date");
            String depreciationRateStr = ctx.formParam("depreciation_rate");

            if (accountIdStr == null || purchasePriceStr == null || currentValueStr == null || purchaseDateStr == null || depreciationRateStr == null) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID accountId = UUID.fromString(accountIdStr);
            long purchasePrice = Long.parseLong(purchasePriceStr);
            long currentValue = Long.parseLong(currentValueStr);
            LocalDate purchaseDate = LocalDate.parse(purchaseDateStr);
            double depreciationRate = Double.parseDouble(depreciationRateStr);

            AccountOtherAssetsService.createAsset(userId, accountId, assetType, purchasePrice, currentValue, purchaseDate, depreciationRate);
            ctx.status(200).result("Asset created successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void readAsset(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            String assetIdStr = ctx.pathParam("asset_id");

            if (assetIdStr == null) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID assetId = UUID.fromString(assetIdStr);
            AccountOtherAsset asset = AccountOtherAssetsService.readAsset(userId, assetId);
            ctx.json(asset);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void updateAsset(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            String assetIdStr = ctx.pathParam("asset_id");
            String accountIdStr = ctx.formParam("account_id");
            String assetType = ctx.formParam("asset_type");
            String purchasePriceStr = ctx.formParam("purchase_price");
            String currentValueStr = ctx.formParam("current_value");
            String purchaseDateStr = ctx.formParam("purchase_date");
            String depreciationRateStr = ctx.formParam("depreciation_rate");

            if (assetIdStr == null || accountIdStr == null || purchasePriceStr == null || currentValueStr == null || purchaseDateStr == null || depreciationRateStr == null) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID assetId = UUID.fromString(assetIdStr);
            UUID accountId = UUID.fromString(accountIdStr);
            long purchasePrice = Long.parseLong(purchasePriceStr);
            long currentValue = Long.parseLong(currentValueStr);
            LocalDate purchaseDate = LocalDate.parse(purchaseDateStr);
            double depreciationRate = Double.parseDouble(depreciationRateStr);

            AccountOtherAssetsService.updateAsset(userId, assetId, accountId, assetType, purchasePrice, currentValue, purchaseDate, depreciationRate);
            ctx.status(200).result("Asset updated successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void deleteAsset(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            String assetIdStr = ctx.pathParam("asset_id");

            if (assetIdStr == null) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID assetId = UUID.fromString(assetIdStr);
            AccountOtherAssetsService.deleteAsset(userId, assetId);
            ctx.status(200).result("Asset deleted successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void applyDepreciation(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            String assetIdStr = ctx.pathParam("asset_id");
            String yearsStr = ctx.queryParam("years");

            if (assetIdStr == null || yearsStr == null) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID assetId = UUID.fromString(assetIdStr);
            int years = Integer.parseInt(yearsStr);

            AccountOtherAssetsService.applyDepreciation(userId, assetId, years);
            ctx.status(200).result("Depreciation applied successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }
}
