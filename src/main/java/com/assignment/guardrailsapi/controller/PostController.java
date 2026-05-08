package com.assignment.guardrailsapi.controller;

import com.assignment.guardrailsapi.dto.CreateCommentRequest;
import com.assignment.guardrailsapi.dto.CreatePostRequest;
import com.assignment.guardrailsapi.entity.Post;
import com.assignment.guardrailsapi.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody CreatePostRequest request) {
        return ResponseEntity.ok(postService.createPost(request));
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<String> addComment(@PathVariable Long postId,
                                             @RequestBody CreateCommentRequest request) {
        postService.addComment(postId, request);
        return ResponseEntity.ok("Comment added successfully");
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<Post> likePost(@PathVariable Long postId,
                                         @RequestParam Long userId) {
        return ResponseEntity.ok(postService.likePost(postId, userId));
    }
}