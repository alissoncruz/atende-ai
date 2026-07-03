package com.atendeai.modules.customer.dto;

import jakarta.validation.constraints.NotBlank;

public record CustomerRequest(
        @NotBlank String name,
        String email,
        String phone,
        String document,
        String address,
        String notes
) {}
