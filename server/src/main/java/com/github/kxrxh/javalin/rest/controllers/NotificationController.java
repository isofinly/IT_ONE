package com.github.kxrxh.javalin.rest.controllers;

import java.util.UUID;

import com.github.kxrxh.javalin.rest.api.jwt.Utils;
import com.github.kxrxh.javalin.rest.services.NotificationService;

import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotificationController {

    private NotificationController() {
    }

    public static void setNotification(Context ctx) {
        try {
            UUID userId = Utils.getUUIDFromContext(ctx);
            String notificationType = ctx.formParam("notification_type");
            String thresholdStr = ctx.formParam("threshold");

            Long threshold = Long.parseLong(thresholdStr);

            NotificationService.setNotification(userId, notificationType, threshold);
            ctx.status(200).result("Notification set successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }
}
