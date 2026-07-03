package com.atendeai.modules.scheduling.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record AppointmentRequest(
        @NotNull UUID customerId,
        @NotBlank String serviceType,
        @NotBlank String title,
        String description,
        @NotNull Instant scheduledAt,
        Integer durationMinutes,
        String notes
) {}
