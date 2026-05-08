package com.assignment.guardrailsapi.repository;

import com.assignment.guardrailsapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}