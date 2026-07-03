package com.atendeai.modules.chatbot.controller;

import com.atendeai.modules.chatbot.dto.*;
import com.atendeai.modules.chatbot.service.ChatService;
import com.atendeai.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/start")
    public ResponseEntity<ApiResponse<ConversationResponse>> start(
            @Valid @RequestBody StartConversationRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(chatService.startConversation(req)));
    }

    @PostMapping("/{id}/message")
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
            @PathVariable UUID id,
            @Valid @RequestBody SendMessageRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(chatService.sendMessage(id, req)));
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getMessages(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(chatService.getMessages(id)));
    }

    @PostMapping("/{id}/end")
    public ResponseEntity<ApiResponse<Void>> end(@PathVariable UUID id) {
        chatService.endConversation(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Conversa encerrada"));
    }
}
