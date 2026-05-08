package com.assignment.guardrailsapi.repository;

import com.assignment.guardrailsapi.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}