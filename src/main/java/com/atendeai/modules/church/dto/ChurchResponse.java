package com.atendeai.modules.church.dto;

import com.atendeai.modules.church.model.Church;
import com.atendeai.modules.church.model.Church.ChurchType;

import java.util.UUID;

public record ChurchResponse(
        UUID id,
        String name,
        ChurchType type,
        String address
) {
    public static ChurchResponse from(Church c) {
        return new ChurchResponse(c.getId(), c.getName(), c.getType(), c.getAddress());
    }
}
