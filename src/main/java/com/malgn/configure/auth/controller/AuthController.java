package com.malgn.configure.auth.controller;

import com.malgn.configure.auth.dto.LoginRequest;
import com.malgn.configure.auth.dto.LoginResponse;
import com.malgn.configure.auth.service.AuthService;
import com.malgn.configure.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    /**
     * 로그인 후 JWT 토큰 반환
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = authService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * accessToken 재발급
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<String>> reissue(@RequestHeader("Refresh-Token") String refreshToken) {
        String newAccessToken = authService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(ApiResponse.success(newAccessToken));
    }
}
