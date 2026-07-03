package com.atendeai.modules.auth.dto;

import com.atendeai.modules.auth.model.UserRole;

import java.util.UUID;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        UserInfo user
) {
    public record UserInfo(UUID id, String email, String name, UserRole role) {}
}
