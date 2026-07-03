package com.atendeai.modules.auth.controller;

import com.atendeai.modules.auth.dto.LoginRequest;
import com.atendeai.modules.auth.dto.RefreshRequest;
import com.atendeai.modules.auth.dto.TokenResponse;
import com.atendeai.modules.auth.service.AuthService;
import com.atendeai.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(authService.login(request)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(authService.refresh(request)));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        // stateless — apenas confirma ao cliente
        return ResponseEntity.ok(ApiResponse.ok(null, "Logout realizado"));
    }
}
