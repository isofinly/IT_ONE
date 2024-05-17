package com.github.kxrxh.javalin.rest.database.models;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Notification {

    private Long notificationId;
    private Long userId;
    private String notificationType;
    private Long threshold;
}
