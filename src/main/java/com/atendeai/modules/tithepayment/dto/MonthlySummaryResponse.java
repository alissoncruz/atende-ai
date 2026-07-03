package com.atendeai.modules.tithepayment.dto;

import java.math.BigDecimal;

public record MonthlySummaryResponse(
        long totalActive,
        long paidCount,
        long pendingCount,
        BigDecimal totalAmount
) {}
