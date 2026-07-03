package com.atendeai.modules.tither.controller;

import com.atendeai.modules.tither.dto.TitherRequest;
import com.atendeai.modules.tither.dto.TitherResponse;
import com.atendeai.modules.tither.service.TitherService;
import com.atendeai.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tithers")
@RequiredArgsConstructor
public class TitherController {

    private final TitherService service;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<TitherResponse>>> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) UUID churchId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(service.list(q, churchId, page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TitherResponse>> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(service.get(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TitherResponse>> create(@Valid @RequestBody TitherRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(service.create(req)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TitherResponse>> update(
            @PathVariable UUID id, @Valid @RequestBody TitherRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(service.update(id, req)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Dizimista removido"));
    }
}
