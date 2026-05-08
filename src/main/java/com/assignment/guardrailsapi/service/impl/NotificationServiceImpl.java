package com.assignment.guardrailsapi.service.impl;

import com.assignment.guardrailsapi.service.NotificationService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class NotificationServiceImpl
        implements NotificationService {

    private final StringRedisTemplate redisTemplate;

    public NotificationServiceImpl(
            StringRedisTemplate redisTemplate) {

        this.redisTemplate = redisTemplate;
    }

    @Override
    public void handleBotNotification(Long userId,
                                      String message) {

        String cooldownKey =
                "notif:user:" + userId;

        Boolean exists =
                redisTemplate.hasKey(cooldownKey);

        // =========================
        // USER ALREADY NOTIFIED
        // =========================

        if (Boolean.TRUE.equals(exists)) {

            redisTemplate.opsForList()
                    .rightPush(
                            "user:" + userId + ":pending_notifs",
                            message
                    );

        }

        // =========================
        // SEND IMMEDIATE NOTIFICATION
        // =========================

        else {

            System.out.println(
                    "Push Notification Sent to User: "
                            + message
            );

            redisTemplate.opsForValue()
                    .set(
                            cooldownKey,
                            "ACTIVE",
                            Duration.ofMinutes(15)
                    );
        }
    }
}