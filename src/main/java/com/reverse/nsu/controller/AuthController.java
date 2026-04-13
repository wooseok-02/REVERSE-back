package com.reverse.nsu.controller;

import com.reverse.nsu.dto.LoginRequestDto;
import com.reverse.nsu.dto.TokenRefreshRequestDto;
import com.reverse.nsu.dto.TokenResponseDto;
import com.reverse.nsu.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 로그인 → 액세스 토큰 + 리프레시 토큰 발급
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(
            @RequestBody LoginRequestDto dto,
            HttpServletRequest request
    ) {
        String ip = request.getRemoteAddr();
        String deviceInfo = request.getHeader("User-Agent");
        return ResponseEntity.ok(authService.login(dto, ip, deviceInfo));
    }

    // 액세스 토큰 재발급 (리프레시 토큰 사용)
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refresh(
            @RequestBody TokenRefreshRequestDto dto
    ) {
        return ResponseEntity.ok(authService.refresh(dto.getRefreshToken()));
    }

    // 로그아웃 → 리프레시 토큰 폐기
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestBody TokenRefreshRequestDto dto
    ) {
        authService.logout(dto.getRefreshToken());
        return ResponseEntity.ok("로그아웃 완료");
    }
}
