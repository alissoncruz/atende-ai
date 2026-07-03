package com.atendeai.modules.church.dto;

import com.atendeai.modules.church.model.Church.ChurchType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChurchRequest(
        @NotBlank String name,
        @NotNull ChurchType type,
        String address
) {}
