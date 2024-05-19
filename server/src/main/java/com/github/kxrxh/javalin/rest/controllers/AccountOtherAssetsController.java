package com.github.kxrxh.javalin.rest.controllers;

import com.github.kxrxh.javalin.rest.api.jwt.Utils;
import com.github.kxrxh.javalin.rest.database.models.AccountOtherAsset;
import com.github.kxrxh.javalin.rest.services.AccountOtherAssetsService;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.UUID;

@Slf4j
public class AccountOtherAssetsController extends AbstractController {

    private AccountOtherAssetsController() {
    }

    public static void createAsset(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);
        JSONObject requestBody;
        try {
            requestBody = new JSONObject(ctx.body());
        } catch (JSONException e) {
            ctx.status(400).result(WRONG_BODY_FORMAT + e.getMessage());
            return;
        }
        String accountIdStr = requestBody.optString("account_id");
        String assetType = requestBody.optString("asset_type");
        String purchasePriceStr = requestBody.optString("purchase_price");
        String currentValueStr = requestBody.optString("current_value");
        String purchaseDateStr = requestBody.optString("purchase_date");
        String depreciationRateStr = requestBody.optString("depreciation_rate");

        if (accountIdStr == null || purchasePriceStr == null || currentValueStr == null || purchaseDateStr == null
                || depreciationRateStr == null) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        UUID accountId = UUID.fromString(accountIdStr);
        long purchasePrice = Long.parseLong(purchasePriceStr);
        long currentValue = Long.parseLong(currentValueStr);
        LocalDate purchaseDate = LocalDate.parse(purchaseDateStr);
        double depreciationRate = Double.parseDouble(depreciationRateStr);
        try {
            AccountOtherAssetsService.createAsset(userId, accountId, assetType, purchasePrice, currentValue,
                    purchaseDate, depreciationRate);
            ctx.status(200).result("Asset created successfully");
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    public static void readAsset(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);

        String assetIdStr = ctx.queryParam(ASSET_ID);

        if (assetIdStr == null || assetIdStr.isEmpty()) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        UUID assetId = UUID.fromString(assetIdStr);

        try {
            AccountOtherAsset asset = AccountOtherAssetsService.readAsset(userId, assetId);
            ctx.json(asset);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    public static void updateAsset(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);
        JSONObject requestBody;
        try {
            requestBody = new JSONObject(ctx.body());
        } catch (JSONException e) {
            ctx.status(400).result(WRONG_BODY_FORMAT + e.getMessage());
            return;
        }
        String assetIdStr = requestBody.optString("asset_id");
        String accountIdStr = requestBody.optString("account_id");
        String assetType = requestBody.optString("asset_type");
        String purchasePriceStr = requestBody.optString("purchase_price");
        String currentValueStr = requestBody.optString("current_value");
        String purchaseDateStr = requestBody.optString("purchase_date");
        String depreciationRateStr = requestBody.optString("depreciation_rate");

        if (assetIdStr.isEmpty() || accountIdStr.isEmpty() || purchasePriceStr.isEmpty()
                || currentValueStr.isEmpty() || purchaseDateStr.isEmpty() || depreciationRateStr.isEmpty()) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        UUID assetId = UUID.fromString(assetIdStr);
        UUID accountId = UUID.fromString(accountIdStr);
        long purchasePrice = Long.parseLong(purchasePriceStr);
        long currentValue = Long.parseLong(currentValueStr);
        LocalDate purchaseDate = LocalDate.parse(purchaseDateStr);
        double depreciationRate = Double.parseDouble(depreciationRateStr);

        try {
            AccountOtherAssetsService.updateAsset(userId, assetId, accountId, assetType, purchasePrice, currentValue,
                    purchaseDate, depreciationRate);
            ctx.status(200).result("Asset updated successfully");
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    public static void deleteAsset(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);
        JSONObject requestBody;
        try {
            requestBody = new JSONObject(ctx.body());
        } catch (JSONException e) {
            ctx.status(400).result(WRONG_BODY_FORMAT + e.getMessage());
            return;
        }
        String assetIdStr = requestBody.optString(ASSET_ID);

        if (assetIdStr.isEmpty()) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        UUID assetId = UUID.fromString(assetIdStr);
        try {
            AccountOtherAssetsService.deleteAsset(userId, assetId);
            ctx.status(200).result("Asset deleted successfully");
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    public static void applyDepreciation(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);
        JSONObject requestBody;
        try {
            requestBody = new JSONObject(ctx.body());
        } catch (JSONException e) {
            ctx.status(400).result(WRONG_BODY_FORMAT + e.getMessage());
            return;
        }
        String assetIdStr = requestBody.optString(ASSET_ID);
        String yearsStr = requestBody.optString("years");

        if (yearsStr == null || assetIdStr.isEmpty() || yearsStr.isEmpty()) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        UUID assetId = UUID.fromString(assetIdStr);
        int years = Integer.parseInt(yearsStr);
        try {
            AccountOtherAssetsService.applyDepreciation(userId, assetId, years);
            ctx.status(200).result("Depreciation applied successfully");
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }
}
