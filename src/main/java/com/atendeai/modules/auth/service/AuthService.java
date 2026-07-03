package com.atendeai.modules.auth.service;

import com.atendeai.config.JwtUtil;
import com.atendeai.modules.auth.dto.LoginRequest;
import com.atendeai.modules.auth.dto.RefreshRequest;
import com.atendeai.modules.auth.dto.TokenResponse;
import com.atendeai.modules.auth.model.User;
import com.atendeai.modules.auth.repository.UserRepository;
import com.atendeai.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public TokenResponse login(LoginRequest request) {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        } catch (AuthenticationException e) {
            throw new BusinessException("Credenciais inválidas", HttpStatus.UNAUTHORIZED);
        }

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado", HttpStatus.NOT_FOUND));

        return buildTokenResponse(user);
    }

    public TokenResponse refresh(RefreshRequest request) {
        if (!jwtUtil.isValid(request.refreshToken())) {
            throw new BusinessException("Refresh token inválido", HttpStatus.UNAUTHORIZED);
        }
        String email = jwtUtil.extractEmail(request.refreshToken());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado", HttpStatus.NOT_FOUND));

        return buildTokenResponse(user);
    }

    private TokenResponse buildTokenResponse(User user) {
        String access = jwtUtil.generateAccessToken(user.getEmail());
        String refresh = jwtUtil.generateRefreshToken(user.getEmail());
        return new TokenResponse(access, refresh,
                new TokenResponse.UserInfo(user.getId(), user.getEmail(), user.getName(), user.getRole()));
    }
}
