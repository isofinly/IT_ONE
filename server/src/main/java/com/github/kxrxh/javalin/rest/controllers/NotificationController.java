package com.github.kxrxh.javalin.rest.controllers;

import com.github.kxrxh.javalin.rest.services.NotificationService;
import io.javalin.http.Context;

public class NotificationController {

    private static final NotificationService notificationService = new NotificationService();

    public static void setNotification(Context ctx) {
        try {
            String userIdStr = ctx.formParam("user_id");
            String notificationType = ctx.formParam("notification_type");
            String thresholdStr = ctx.formParam("threshold");

            if (userIdStr == null || notificationType == null || thresholdStr == null) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            Long userId = Long.parseLong(userIdStr);
            Long threshold = Long.parseLong(thresholdStr);

            notificationService.setNotification(userId, notificationType, threshold);
            ctx.status(200).result("Notification set successfully");
        } catch (Exception e) {
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }
}

