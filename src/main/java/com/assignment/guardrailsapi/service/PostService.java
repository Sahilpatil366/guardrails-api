package com.assignment.guardrailsapi.service;

import com.assignment.guardrailsapi.dto.CreateCommentRequest;
import com.assignment.guardrailsapi.dto.CreatePostRequest;
import com.assignment.guardrailsapi.entity.Post;

public interface PostService {

    Post createPost(CreatePostRequest request);

    Post likePost(Long postId, Long userId);

    void addComment(Long postId, CreateCommentRequest request);
}