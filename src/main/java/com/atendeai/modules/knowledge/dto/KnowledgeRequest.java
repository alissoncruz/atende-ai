package com.atendeai.modules.knowledge.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record KnowledgeRequest(
        UUID customerId,
        @NotBlank String title,
        @NotBlank String content,
        String category
) {}
