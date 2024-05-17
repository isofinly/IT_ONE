package com.github.kxrxh.javalin.rest.controllers;

import java.util.UUID;

import com.github.kxrxh.javalin.rest.services.NotificationService;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotificationController {

    private NotificationController() {
    }

    public static void setNotification(Context ctx) {
        try {
            String userIdStr = ctx.formParam("user_id");
            String notificationType = ctx.formParam("notification_type");
            String thresholdStr = ctx.formParam("threshold");

            if (userIdStr == null || notificationType == null || thresholdStr == null) {
                ctx.status(400).result("Missing required parameters");
                return;
            }

            UUID userId = UUID.fromString(userIdStr);
            Long threshold = Long.parseLong(thresholdStr);

            NotificationService.setNotification(userId, notificationType, threshold);
            ctx.status(200).result("Notification set successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }
}
