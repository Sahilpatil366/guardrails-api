package com.assignment.guardrailsapi.scheduler;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class NotificationScheduler {

    private final StringRedisTemplate redisTemplate;

    public NotificationScheduler(
            StringRedisTemplate redisTemplate) {

        this.redisTemplate = redisTemplate;
    }

    @Scheduled(fixedRate = 300000)
    public void sweepNotifications() {

        Set<String> keys =
                redisTemplate.keys("user:*:pending_notifs");

        if (keys == null || keys.isEmpty()) {
            return;
        }

        for (String key : keys) {

            List<String> messages =
                    redisTemplate.opsForList()
                            .range(key, 0, -1);

            if (messages == null || messages.isEmpty()) {
                continue;
            }

            int count = messages.size();

            System.out.println(
                    "Summarized Push Notification: "
                            + messages.get(0)
                            + " and "
                            + (count - 1)
                            + " others interacted with your posts."
            );

            redisTemplate.delete(key);
        }
    }
}