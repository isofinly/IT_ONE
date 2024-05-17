package com.github.kxrxh.javalin.rest.controllers;

import java.util.UUID;

import com.github.kxrxh.javalin.rest.api.jwt.Utils;

import io.javalin.http.Context;

// TODO implement data visualization logic
public class VisualizationController {
    public static void getVisualization(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);
        String visualizationType = ctx.queryParam("visualization_type");
        String dateRange = ctx.queryParam("date_range");
        // Implement data visualization logic here
        ctx.result("Get visualization for user " + userId + " visualization type: " + visualizationType + " date range: " + dateRange);
    }
}
