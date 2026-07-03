package com.atendeai.modules.knowledge.controller;

import com.atendeai.modules.knowledge.dto.KnowledgeRequest;
import com.atendeai.modules.knowledge.dto.KnowledgeResponse;
import com.atendeai.modules.knowledge.service.KnowledgeService;
import com.atendeai.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/knowledge")
@RequiredArgsConstructor
public class KnowledgeController {

    private final KnowledgeService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<KnowledgeResponse>>> list(
            @RequestParam(required = false) UUID customerId) {
        return ResponseEntity.ok(ApiResponse.ok(service.list(customerId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<KnowledgeResponse>> create(@Valid @RequestBody KnowledgeRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(service.create(req)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Documento removido"));
    }
}
