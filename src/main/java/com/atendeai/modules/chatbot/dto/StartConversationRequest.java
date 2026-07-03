package com.atendeai.modules.chatbot.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record StartConversationRequest(@NotNull UUID customerId) {}
