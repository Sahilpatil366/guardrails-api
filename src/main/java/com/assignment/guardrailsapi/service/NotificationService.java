package com.assignment.guardrailsapi.service;

public interface NotificationService {

    void handleBotNotification(Long userId,
                               String message);
}