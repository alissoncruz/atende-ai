package com.atendeai.modules.church.dto;

import com.atendeai.modules.church.model.Church;

import java.util.UUID;

public record ChurchResponse(
        UUID id,
        String name,
        String address
) {
    public static ChurchResponse from(Church c) {
        return new ChurchResponse(c.getId(), c.getName(), c.getAddress());
    }
}
