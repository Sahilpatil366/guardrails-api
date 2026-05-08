package com.assignment.guardrailsapi.service;

import com.assignment.guardrailsapi.entity.InteractionType;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisGuardService {

    private final StringRedisTemplate redisTemplate;

    public RedisGuardService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // =========================
    // VIRALITY SCORE
    // =========================

    public Long incrementViralityScore(Long postId,
                                       InteractionType interactionType) {

        String key = "post:" + postId + ":virality_score";

        long points = switch (interactionType) {
            case BOT_REPLY -> 1;
            case HUMAN_LIKE -> 20;
            case HUMAN_COMMENT -> 50;
        };

        Long score = redisTemplate.opsForValue().increment(key, points);
        return score == null ? 0L : score;
    }

    // =========================
    // HORIZONTAL CAP
    // MAX 100 BOT REPLIES
    // =========================

    public boolean allowBotReply(Long postId) {
        String key = "post:" + postId + ":bot_count";

        Long count = redisTemplate.opsForValue().increment(key);
        return count != null && count <= 100;
    }

    // =========================
    // COOLDOWN CAP
    // 10 MINUTES
    // =========================

    public boolean checkCooldown(Long botId,
                                 Long humanId) {

        String key = "cooldown:bot_" + botId + ":human_" + humanId;

        Boolean locked = redisTemplate.opsForValue()
                .setIfAbsent(key, "ACTIVE", Duration.ofMinutes(10));

        return Boolean.TRUE.equals(locked);
    }

    // =========================
    // GET VIRALITY SCORE
    // =========================

    public Long getViralityScore(Long postId) {
        String key = "post:" + postId + ":virality_score";
        String value = redisTemplate.opsForValue().get(key);

        return value == null ? 0L : Long.parseLong(value);
    }
}
