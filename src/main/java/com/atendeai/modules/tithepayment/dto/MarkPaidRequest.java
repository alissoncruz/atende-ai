package com.atendeai.modules.tithepayment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record MarkPaidRequest(
        @NotNull UUID titherId,
        @NotBlank @Pattern(regexp = "\\d{4}-\\d{2}", message = "formato esperado: yyyy-MM") String referenceMonth,
        BigDecimal amount,
        LocalDate paidAt,
        String notes
) {}
