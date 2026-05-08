package com.assignment.guardrailsapi.repository;

import com.assignment.guardrailsapi.entity.Bot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BotRepository extends JpaRepository<Bot, Long> {
}