package com.atendeai.modules.chatbot.dto;

import com.atendeai.modules.chatbot.model.Message;
import java.time.Instant;
import java.util.UUID;

public record MessageResponse(UUID id, String role, String content, Instant createdAt) {
    public static MessageResponse from(Message m) {
        return new MessageResponse(m.getId(), m.getRole(), m.getContent(), m.getCreatedAt());
    }
}
