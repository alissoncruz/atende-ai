package com.atendeai.modules.chatbot.dto;

import com.atendeai.modules.chatbot.model.Conversation;
import java.time.Instant;
import java.util.UUID;

public record ConversationResponse(UUID id, UUID customerId, String channel, Instant startedAt) {
    public static ConversationResponse from(Conversation c) {
        return new ConversationResponse(
                c.getId(), c.getCustomer().getId(), c.getChannel(), c.getStartedAt());
    }
}
