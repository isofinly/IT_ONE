package com.github.kxrxh.javalin.rest.controllers;

import java.util.UUID;

import com.github.kxrxh.javalin.rest.api.jwt.Utils;
import com.github.kxrxh.javalin.rest.database.models.Category;
import com.github.kxrxh.javalin.rest.entities.CategoryAnalysisResult;
import com.github.kxrxh.javalin.rest.services.CategoryService;

import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CategoryController {

    private CategoryController() {
    }

    public static void createCategory(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            String name = ctx.formParam("name");

            if (name == null) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            Category category = CategoryService.createCategory(userId, name);
            ctx.status(201).json(category);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void readCategory(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            String categoryIdStr = ctx.pathParam("category_id");

            if (categoryIdStr == null) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID categoryId = UUID.fromString(categoryIdStr);
            Category category = CategoryService.readCategory(userId, categoryId);
            ctx.json(category);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void updateCategory(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            String categoryIdStr = ctx.pathParam("category_id");
            String name = ctx.formParam("name");

            if (categoryIdStr == null || name == null) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID categoryId = UUID.fromString(categoryIdStr);
            Category category = CategoryService.updateCategory(userId, categoryId, name);
            ctx.json(category);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void deleteCategory(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            String categoryIdStr = ctx.pathParam("category_id");

            if (categoryIdStr == null) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID categoryId = UUID.fromString(categoryIdStr);
            CategoryService.deleteCategory(userId, categoryId);
            ctx.status(204).result("Category deleted successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void analyzeCategory(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            String categoryIdStr = ctx.queryParam("category_id");
            String dateRange = ctx.queryParam("date_range");

            UUID categoryId = UUID.fromString(categoryIdStr);
            CategoryAnalysisResult analysisResult = CategoryService.analyzeCategory(userId, categoryId, dateRange);
            ctx.json(analysisResult);
        } catch (Exception e) {
            log.error(e.getMessage());
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }
}
