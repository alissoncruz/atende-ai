package com.atendeai.modules.tither.dto;

import com.atendeai.modules.tither.model.Tither;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TitherResponse(
        UUID id,
        String name,
        String cpf,
        String phone,
        String email,
        LocalDate birthDate,
        String zipCode,
        String street,
        String number,
        String neighborhood,
        String city,
        String state,
        UUID churchId,
        String churchName,
        LocalDate startDate,
        BigDecimal referenceAmount,
        boolean active,
        Instant createdAt
) {
    public static TitherResponse from(Tither t) {
        return new TitherResponse(
                t.getId(), t.getName(), t.getCpf(), t.getPhone(), t.getEmail(), t.getBirthDate(),
                t.getZipCode(), t.getStreet(), t.getNumber(), t.getNeighborhood(), t.getCity(), t.getState(),
                t.getChurch().getId(), t.getChurch().getName(),
                t.getStartDate(), t.getReferenceAmount(), t.isActive(), t.getCreatedAt());
    }
}
