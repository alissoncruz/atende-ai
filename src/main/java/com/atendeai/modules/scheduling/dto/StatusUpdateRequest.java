package com.atendeai.modules.scheduling.dto;

import com.atendeai.modules.scheduling.model.Appointment;
import jakarta.validation.constraints.NotNull;

public record StatusUpdateRequest(@NotNull Appointment.AppointmentStatus status) {}
