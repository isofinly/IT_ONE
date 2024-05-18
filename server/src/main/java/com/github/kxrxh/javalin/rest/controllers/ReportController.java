package com.github.kxrxh.javalin.rest.controllers;

import com.github.kxrxh.javalin.rest.api.jwt.Utils;
import com.github.kxrxh.javalin.rest.services.ReportService;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class ReportController extends AbstractController {

    private ReportController() {
    }

    public static void generateMonthlyReport(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);

        String yearStr = ctx.queryParam("year");
        String monthStr = ctx.queryParam("month");

        if (yearStr == null || monthStr == null || yearStr.isEmpty() || monthStr.isEmpty()) {
            ctx.status(400).result(MISSING_REQUIERED_STRING);
            return;
        }

        try {
            int year = Integer.parseInt(yearStr);
            int month = Integer.parseInt(monthStr);

            Map<String, Object> reportData = ReportService.generateMonthlyReport(userId, year, month);
            ctx.status(200).json(reportData);
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid year or month format");
        } catch (SQLException e) {
            log.error("Error generating monthly report", e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        }
    }
}
