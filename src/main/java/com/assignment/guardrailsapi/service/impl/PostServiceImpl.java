package com.assignment.guardrailsapi.service.impl;

import com.assignment.guardrailsapi.dto.CreateCommentRequest;
import com.assignment.guardrailsapi.dto.CreatePostRequest;
import com.assignment.guardrailsapi.entity.AuthorType;
import com.assignment.guardrailsapi.entity.Comment;
import com.assignment.guardrailsapi.entity.InteractionType;
import com.assignment.guardrailsapi.entity.Post;
import com.assignment.guardrailsapi.repository.CommentRepository;
import com.assignment.guardrailsapi.repository.PostRepository;
import com.assignment.guardrailsapi.service.NotificationService;
import com.assignment.guardrailsapi.service.PostService;
import com.assignment.guardrailsapi.service.RedisGuardService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final RedisGuardService redisGuardService;
    private final NotificationService notificationService;

    public PostServiceImpl(PostRepository postRepository,
                           CommentRepository commentRepository,
                           RedisGuardService redisGuardService,
                           NotificationService notificationService) {

        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.redisGuardService = redisGuardService;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public Post createPost(CreatePostRequest request) {

        Post post = new Post();

        post.setAuthorId(request.getAuthorId());
        post.setAuthorType(request.getAuthorType());
        post.setContent(request.getContent());
        post.setCreatedAt(LocalDateTime.now());

        return postRepository.save(post);
    }

    @Override
    @Transactional
    public Post likePost(Long postId, Long userId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Post not found"
                ));

        redisGuardService.incrementViralityScore(
                postId,
                InteractionType.HUMAN_LIKE
        );

        return postRepository.save(post);
    }

    @Override
    @Transactional
    public void addComment(Long postId,
                           CreateCommentRequest request) {

        // =========================
        // VERTICAL CAP
        // =========================

        if (request.getDepthLevel() > 20) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Comment depth cannot exceed 20"
            );
        }

        // =========================
        // FETCH POST
        // =========================

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Post not found"
                ));

        // =========================
        // BOT GUARDRAILS
        // =========================

        if (request.getAuthorType() == AuthorType.BOT) {

            // Horizontal Cap
            boolean allowed =
                    redisGuardService.allowBotReply(postId);

            if (!allowed) {
                throw new ResponseStatusException(
                        HttpStatus.TOO_MANY_REQUESTS,
                        "Bot reply limit exceeded"
                );
            }

            // Cooldown Cap
            if (post.getAuthorType() != AuthorType.USER) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Bot cooldown applies only when replying to a human user"
                );
            }

            boolean cooldownAllowed =
                    redisGuardService.checkCooldown(
                            request.getAuthorId(),
                            post.getAuthorId()
                    );

            if (!cooldownAllowed) {

                throw new ResponseStatusException(
                        HttpStatus.TOO_MANY_REQUESTS,
                        "Bot cooldown active"
                );
            }
        }

        // =========================
        // SAVE COMMENT
        // =========================

        Comment comment = new Comment();

        comment.setPostId(postId);
        comment.setAuthorId(request.getAuthorId());
        comment.setAuthorType(request.getAuthorType());
        comment.setContent(request.getContent());
        comment.setDepthLevel(request.getDepthLevel());
        comment.setCreatedAt(LocalDateTime.now());

        commentRepository.save(comment);

        // =========================
        // VIRALITY + NOTIFICATIONS
        // =========================

        if (request.getAuthorType() == AuthorType.BOT) {

            redisGuardService.incrementViralityScore(
                    postId,
                    InteractionType.BOT_REPLY
            );

            notificationService.handleBotNotification(
                    post.getAuthorId(),
                    "Bot " + request.getAuthorId()
                            + " replied to your post"
            );

        } else {

            redisGuardService.incrementViralityScore(
                    postId,
                    InteractionType.HUMAN_COMMENT
            );
        }
    }
}