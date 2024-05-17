package com.github.kxrxh.javalin.rest.controllers;

import com.github.kxrxh.javalin.rest.database.models.Report;
import com.github.kxrxh.javalin.rest.services.ReportService;
import io.javalin.http.Context;

public class ReportController {

    private static final ReportService reportService = new ReportService();

    public static void generateReport(Context ctx) {
        try {
            String userIdStr = ctx.formParam("user_id");
            String reportType = ctx.formParam("report_type");
            String dateRange = ctx.formParam("date_range");

            if (userIdStr == null || reportType == null || dateRange == null) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            Long userId = Long.parseLong(userIdStr);

            Report report = reportService.generateReport(userId, reportType, dateRange);
            ctx.status(200).json(report);
        } catch (Exception e) {
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void getReport(Context ctx) {
        try {
            String reportIdStr = ctx.pathParam("report_id");

            Long reportId = Long.parseLong(reportIdStr);

            Report report = reportService.getReport(reportId);
            if (report == null) {
                ctx.status(404).result("Report not found");
            } else {
                ctx.status(200).json(report);
            }
        } catch (Exception e) {
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }
}
