package com.atendeai.modules.scheduling.controller;

import com.atendeai.modules.scheduling.dto.AppointmentRequest;
import com.atendeai.modules.scheduling.dto.AppointmentResponse;
import com.atendeai.modules.scheduling.dto.StatusUpdateRequest;
import com.atendeai.modules.scheduling.service.AppointmentService;
import com.atendeai.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService service;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AppointmentResponse>>> list(
            @RequestParam(required = false) UUID customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(service.list(customerId, page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AppointmentResponse>> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(service.get(id)));
    }

    @GetMapping("/schedule")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> schedule(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return ResponseEntity.ok(ApiResponse.ok(service.getSchedule(from, to)));
    }

    @GetMapping("/availability")
    public ResponseEntity<ApiResponse<List<String>>> availability(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant date,
            @RequestParam(required = false) String service) {
        return ResponseEntity.ok(ApiResponse.ok(this.service.getAvailableSlots(date, service)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AppointmentResponse>> create(@Valid @RequestBody AppointmentRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(service.create(req)));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<AppointmentResponse>> updateStatus(
            @PathVariable UUID id, @Valid @RequestBody StatusUpdateRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(service.updateStatus(id, req)));
    }
}
