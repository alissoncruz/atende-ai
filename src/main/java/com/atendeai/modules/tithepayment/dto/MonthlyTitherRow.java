package com.atendeai.modules.tithepayment.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record MonthlyTitherRow(
        UUID titherId,
        String titherName,
        UUID churchId,
        String churchName,
        BigDecimal referenceAmount,
        String status, // PAGO | PENDENTE
        UUID paymentId,
        BigDecimal amountPaid,
        Instant paidAt
) {}
