package com.atendeai.modules.scheduling.dto;

import com.atendeai.modules.scheduling.model.Appointment;

import java.time.Instant;
import java.util.UUID;

public record AppointmentResponse(
        UUID id,
        UUID customerId,
        String customerName,
        String serviceType,
        String title,
        String description,
        Instant scheduledAt,
        int durationMinutes,
        Appointment.AppointmentStatus status,
        String notes,
        Instant createdAt
) {
    public static AppointmentResponse from(Appointment a) {
        return new AppointmentResponse(
                a.getId(),
                a.getCustomer().getId(),
                a.getCustomer().getName(),
                a.getServiceType(),
                a.getTitle(),
                a.getDescription(),
                a.getScheduledAt(),
                a.getDurationMinutes(),
                a.getStatus(),
                a.getNotes(),
                a.getCreatedAt());
    }
}
