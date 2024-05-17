package com.github.kxrxh.javalin.rest.controllers;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

import com.github.kxrxh.javalin.rest.database.models.Report;
import com.github.kxrxh.javalin.rest.services.ReportService;

import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReportController {

    private ReportController() {
    }

    // TODO: generate per user with user_id
    public static void generateMonthlyReport(Context ctx) {
        try {
            String yearStr = ctx.queryParam("year");
            String monthStr = ctx.queryParam("month");

            if (yearStr == null || monthStr == null) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            int year = Integer.parseInt(yearStr);
            int month = Integer.parseInt(monthStr);

            Map<String, Object> reportData = ReportService.generateMonthlyReport(year, month);
            ctx.status(200).json(reportData);
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid year or month format");
        } catch (SQLException e) {
            log.error("Error generating monthly report", e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    public static void getReport(Context ctx) {
        try {
            String reportIdStr = ctx.pathParam("report_id");

            UUID reportId = UUID.fromString(reportIdStr);

            Report report = ReportService.getReport(reportId);
            if (report == null) {
                ctx.status(404).result("Report not found");
            } else {
                ctx.status(200).json(report);
            }
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid report ID format");
        } catch (SQLException e) {
            log.error("Error fetching report", e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

}