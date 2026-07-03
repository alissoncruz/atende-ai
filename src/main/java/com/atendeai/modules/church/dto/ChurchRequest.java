package com.atendeai.modules.church.dto;

import jakarta.validation.constraints.NotBlank;

public record ChurchRequest(
        @NotBlank String name,
        String address
) {}
