package com.atendeai.modules.tither.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TitherRequest(
        @NotBlank String name,
        @NotBlank String cpf,
        String phone,
        String email,
        LocalDate birthDate,
        String zipCode,
        String street,
        String number,
        String neighborhood,
        String city,
        String state,
        @NotNull UUID churchId,
        LocalDate startDate,
        BigDecimal referenceAmount
) {}
