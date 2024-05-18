package com.github.kxrxh.javalin.rest.controllers;

import com.github.kxrxh.javalin.rest.api.jwt.Utils;
import com.github.kxrxh.javalin.rest.database.models.Category;
import com.github.kxrxh.javalin.rest.entities.CategoryAnalysisResult;
import com.github.kxrxh.javalin.rest.services.CategoryService;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.UUID;

@Slf4j
public class CategoryController extends AbstractController {

    private CategoryController() {
    }

    public static void createCategory(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);
        String name = ctx.formParam("name");

        if (name == null) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        try {
            Category category = CategoryService.createCategory(userId, name);
            ctx.status(201).json(category);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    public static void readCategory(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);
        String categoryIdStr = ctx.pathParam(CATEGORY_ID);

        if (categoryIdStr == null) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        UUID categoryId = UUID.fromString(categoryIdStr);

        try {
            Category category = CategoryService.readCategory(userId, categoryId);
            ctx.json(category);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    public static void updateCategory(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);
        String categoryIdStr = ctx.pathParam(CATEGORY_ID);
        String name = ctx.formParam("name");

        if (categoryIdStr == null || name == null) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        UUID categoryId = UUID.fromString(categoryIdStr);

        try {
            Category category = CategoryService.updateCategory(userId, categoryId, name);
            ctx.json(category);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    public static void deleteCategory(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);
        String categoryIdStr = ctx.pathParam(CATEGORY_ID);

        if (categoryIdStr == null) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        UUID categoryId = UUID.fromString(categoryIdStr);

        try {
            CategoryService.deleteCategory(userId, categoryId);
            ctx.status(204).result("Category deleted successfully");
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }

    public static void analyzeCategory(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);
        String categoryIdStr = ctx.queryParam(CATEGORY_ID);
        String dateRange = ctx.queryParam("date_range");

        UUID categoryId = UUID.fromString(categoryIdStr);

        try {
            CategoryAnalysisResult analysisResult = CategoryService.analyzeCategory(userId, categoryId, dateRange);
            ctx.json(analysisResult);
        } catch (SQLException e) {
            log.error(e.getMessage());
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }
}
