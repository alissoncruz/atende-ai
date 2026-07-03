package com.atendeai.modules.chatbot.repository;

import com.atendeai.modules.chatbot.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
    List<Conversation> findByCustomer_IdOrderByStartedAtDesc(UUID customerId);
}
