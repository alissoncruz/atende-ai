package com.atendeai.modules.church.controller;

import com.atendeai.modules.church.dto.ChurchRequest;
import com.atendeai.modules.church.dto.ChurchResponse;
import com.atendeai.modules.church.service.ChurchService;
import com.atendeai.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/churches")
@RequiredArgsConstructor
public class ChurchController {

    private final ChurchService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ChurchResponse>>> list() {
        return ResponseEntity.ok(ApiResponse.ok(service.list()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ChurchResponse>> create(@Valid @RequestBody ChurchRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(service.create(req)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ChurchResponse>> update(
            @PathVariable UUID id, @Valid @RequestBody ChurchRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(service.update(id, req)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Igreja removida"));
    }
}
