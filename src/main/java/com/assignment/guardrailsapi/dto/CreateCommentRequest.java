package com.assignment.guardrailsapi.dto;

import com.assignment.guardrailsapi.entity.AuthorType;
import lombok.Data;

@Data
public class CreateCommentRequest {

    private Long authorId;

    private AuthorType authorType;

    private String content;

    private int depthLevel;
}