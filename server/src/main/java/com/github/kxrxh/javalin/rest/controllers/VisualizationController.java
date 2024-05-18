package com.github.kxrxh.javalin.rest.controllers;
import org.json.JSONObject;


import com.github.kxrxh.javalin.rest.api.jwt.Utils;
import io.javalin.http.Context;

import java.util.UUID;

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
