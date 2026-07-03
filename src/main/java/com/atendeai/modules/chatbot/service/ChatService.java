package com.atendeai.modules.chatbot.service;

import com.atendeai.modules.chatbot.dto.*;
import com.atendeai.modules.chatbot.model.Conversation;
import com.atendeai.modules.chatbot.model.Message;
import com.atendeai.modules.chatbot.repository.ConversationRepository;
import com.atendeai.modules.chatbot.repository.MessageRepository;
import com.atendeai.modules.customer.model.Customer;
import com.atendeai.modules.customer.repository.CustomerRepository;
import com.atendeai.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final CustomerRepository customerRepository;
    private final ClaudeApiService claudeApiService;

    @Transactional
    public ConversationResponse startConversation(StartConversationRequest req) {
        Customer customer = customerRepository.findById(req.customerId())
                .orElseThrow(() -> new BusinessException("Cliente não encontrado", HttpStatus.NOT_FOUND));

        Conversation conversation = Conversation.builder()
                .customer(customer)
                .channel("WEB")
                .build();

        return ConversationResponse.from(conversationRepository.save(conversation));
    }

    @Transactional
    public MessageResponse sendMessage(UUID conversationId, SendMessageRequest req) {
        Conversation conversation = findConversation(conversationId);

        // Salva mensagem do usuário
        Message userMsg = Message.builder()
                .conversation(conversation)
                .role("user")
                .content(req.content())
                .build();
        messageRepository.save(userMsg);

        // Monta histórico para o Claude
        List<Message> history = messageRepository.findByConversation_IdOrderByCreatedAtAsc(conversationId);
        List<Map<String, Object>> messages = history.stream()
                .filter(m -> "user".equals(m.getRole()) || "assistant".equals(m.getRole()))
                .map(m -> Map.<String, Object>of("role", m.getRole(), "content", m.getContent()))
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));

        // Chama Claude API
        Map<String, Object> claudeResponse = claudeApiService.sendMessage(
                messages,
                claudeApiService.getAvailableTools(),
                null
        );

        // Extrai texto da resposta
        String assistantContent = extractContent(claudeResponse);

        // Salva resposta do assistant
        Message assistantMsg = Message.builder()
                .conversation(conversation)
                .role("assistant")
                .content(assistantContent)
                .build();
        messageRepository.save(assistantMsg);

        return MessageResponse.from(assistantMsg);
    }

    public List<MessageResponse> getMessages(UUID conversationId) {
        findConversation(conversationId); // valida existência
        return messageRepository.findByConversation_IdOrderByCreatedAtAsc(conversationId)
                .stream().map(MessageResponse::from).toList();
    }

    @Transactional
    public void endConversation(UUID conversationId) {
        Conversation conversation = findConversation(conversationId);
        conversation.setEndedAt(Instant.now());
        conversationRepository.save(conversation);
    }

    private Conversation findConversation(UUID id) {
        return conversationRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Conversa não encontrada", HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("unchecked")
    private String extractContent(Map<String, Object> response) {
        try {
            List<Map<String, Object>> content = (List<Map<String, Object>>) response.get("content");
            if (content != null && !content.isEmpty()) {
                return (String) content.get(0).get("text");
            }
        } catch (Exception e) {
            log.error("Erro ao extrair conteúdo da resposta do Claude", e);
        }
        return "Desculpe, não consegui processar sua mensagem.";
    }
}
