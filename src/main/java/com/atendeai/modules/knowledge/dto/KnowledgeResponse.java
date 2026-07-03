package com.atendeai.modules.knowledge.dto;

import com.atendeai.modules.knowledge.model.KnowledgeDocument;

import java.time.Instant;
import java.util.UUID;

public record KnowledgeResponse(UUID id, UUID customerId, String title, String content, String category, Instant createdAt) {
    public static KnowledgeResponse from(KnowledgeDocument d) {
        return new KnowledgeResponse(
                d.getId(),
                d.getCustomer() != null ? d.getCustomer().getId() : null,
                d.getTitle(), d.getContent(), d.getCategory(), d.getCreatedAt());
    }
}
