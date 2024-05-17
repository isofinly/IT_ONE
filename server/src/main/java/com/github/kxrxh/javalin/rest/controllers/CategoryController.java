package com.github.kxrxh.javalin.rest.controllers;

import com.github.kxrxh.javalin.rest.entities.CategoryAnalysisResult;
import com.github.kxrxh.javalin.rest.services.CategoryService;
import io.javalin.http.Context;

public class CategoryController {

    private static final CategoryService categoryService = new CategoryService();

    public static void analyzeCategory(Context ctx) {
        try {
            String categoryIdStr = ctx.pathParam("category_id");
            String dateRange = ctx.queryParam("date_range");

            Long categoryId = Long.parseLong(categoryIdStr);

            CategoryAnalysisResult analysisResult = categoryService.analyzeCategory(categoryId, dateRange);
            ctx.json(analysisResult);
        } catch (Exception e) {
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }
}
