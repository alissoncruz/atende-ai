package com.atendeai.modules.tithepayment.dto;

import java.math.BigDecimal;

public record MonthlyHistoryItem(
        String month, // yyyy-MM
        BigDecimal totalAmount
) {}
