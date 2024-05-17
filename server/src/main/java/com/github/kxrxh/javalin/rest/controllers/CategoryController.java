package com.github.kxrxh.javalin.rest.controllers;

import java.util.UUID;

import com.github.kxrxh.javalin.rest.entities.CategoryAnalysisResult;
import com.github.kxrxh.javalin.rest.services.CategoryService;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CategoryController {
    // TODO: check user_id and other fields from token

    private CategoryController() {
    }

    public static void analyzeCategory(Context ctx) {
        try {
            String categoryIdStr = ctx.pathParam("category_id");
            String dateRange = ctx.queryParam("date_range");

            UUID categoryId = UUID.fromString(categoryIdStr);
            CategoryAnalysisResult analysisResult = CategoryService.analyzeCategory(categoryId, dateRange);
            ctx.json(analysisResult);
        } catch (Exception e) {
            log.error(e.getMessage());
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }
}
