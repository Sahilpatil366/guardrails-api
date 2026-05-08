package com.assignment.guardrailsapi.repository;

import com.assignment.guardrailsapi.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}