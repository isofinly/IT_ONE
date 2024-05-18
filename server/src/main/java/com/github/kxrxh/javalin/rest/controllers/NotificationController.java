package com.github.kxrxh.javalin.rest.controllers;

import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import com.github.kxrxh.javalin.rest.api.jwt.Utils;
import com.github.kxrxh.javalin.rest.services.NotificationService;

import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotificationController extends AbstractController {

    private NotificationController() {
    }

    public static void setNotification(Context ctx) {
        UUID userId = Utils.getUUIDFromContext(ctx);
        JSONObject requestBody;
        try {
            requestBody = new JSONObject(ctx.body());
        } catch (JSONException e) {
            ctx.status(400).result(WRONG_BODY_FORMAT + e.getMessage());
            return;
        }
        String notificationType = requestBody.optString("notification_type");
        String thresholdStr = requestBody.optString("threshold");

        Long threshold = Long.parseLong(thresholdStr);

        try {
            NotificationService.setNotification(userId, notificationType, threshold);
            ctx.status(200).result("Notification set successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.status(500).result(INTERNAL_ERROR + e.getMessage());
        } 
    }
}
