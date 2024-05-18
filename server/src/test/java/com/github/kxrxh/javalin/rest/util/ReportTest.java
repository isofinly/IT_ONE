package com.github.kxrxh.javalin.rest.util;

import com.github.kxrxh.javalin.rest.database.models.Report;
import com.github.kxrxh.javalin.rest.database.models.Transaction;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ReportTest {

    @Test
    void testNoArgsConstructor() {
        Report report = new Report();
        assertNotNull(report, "Report object should not be null");
    }

    @Test
    void testAllArgsConstructor() {
        UUID reportId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String reportType = "Monthly";
        String dateRange = "January 2024";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        List<Transaction> transactions = new ArrayList<>();

        Report report = new Report(reportId, userId, reportType, dateRange, createdAt, updatedAt, transactions);
        assertEquals(reportId, report.getReportId());
        assertEquals(userId, report.getUserId());
        assertEquals(reportType, report.getReportType());
        assertEquals(dateRange, report.getDateRange());
        assertEquals(createdAt, report.getCreatedAt());
        assertEquals(updatedAt, report.getUpdatedAt());
        assertEquals(transactions, report.getTransactions());
    }

    @Test
    void testBuilder() {
        UUID reportId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String reportType = "Yearly";
        String dateRange = "2023";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        List<Transaction> transactions = new ArrayList<>();

        Report report = Report.builder()
                .reportId(reportId)
                .userId(userId)
                .reportType(reportType)
                .dateRange(dateRange)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .transactions(transactions)
                .build();

        assertEquals(reportId, report.getReportId());
        assertEquals(userId, report.getUserId());
        assertEquals(reportType, report.getReportType());
        assertEquals(dateRange, report.getDateRange());
        assertEquals(createdAt, report.getCreatedAt());
        assertEquals(updatedAt, report.getUpdatedAt());
        assertEquals(transactions, report.getTransactions());
    }

    @Test
    void testSettersAndGetters() {
        Report report = new Report();

        UUID reportId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String reportType = "Weekly";
        String dateRange = "Week 1, 2024";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        List<Transaction> transactions = new ArrayList<>();

        report.setReportId(reportId);
        report.setUserId(userId);
        report.setReportType(reportType);
        report.setDateRange(dateRange);
        report.setCreatedAt(createdAt);
        report.setUpdatedAt(updatedAt);
        report.setTransactions(transactions);

        assertEquals(reportId, report.getReportId());
        assertEquals(userId, report.getUserId());
        assertEquals(reportType, report.getReportType());
        assertEquals(dateRange, report.getDateRange());
        assertEquals(createdAt, report.getCreatedAt());
        assertEquals(updatedAt, report.getUpdatedAt());
        assertEquals(transactions, report.getTransactions());
    }
}
