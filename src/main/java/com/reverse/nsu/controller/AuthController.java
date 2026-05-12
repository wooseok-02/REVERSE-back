package com.reverse.nsu.controller;

import com.reverse.nsu.dto.EmailSendRequestDto;
import com.reverse.nsu.dto.EmailVerifyRequestDto;
import com.reverse.nsu.dto.LoginRequestDto;
import com.reverse.nsu.dto.SignUpRequestDto;
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

    // 이메일 인증번호 전송
    @PostMapping("/email/send")
    public ResponseEntity<String> sendEmail(@RequestBody EmailSendRequestDto dto) {
        authService.sendVerificationCode(dto);
        return ResponseEntity.ok("인증번호가 전송되었습니다.");
    }

    // 이메일 인증번호 재전송 (이전 코드 폐기 후 새 코드 발송)
    @PostMapping("/email/resend")
    public ResponseEntity<String> resendEmail(@RequestBody EmailSendRequestDto dto) {
        authService.sendVerificationCode(dto);
        return ResponseEntity.ok("인증번호가 재전송되었습니다.");
    }

    // 인증번호 확인
    @PostMapping("/email/verify")
    public ResponseEntity<String> verifyEmail(@RequestBody EmailVerifyRequestDto dto) {
        authService.verifyEmailCode(dto);
        return ResponseEntity.ok("이메일 인증이 완료되었습니다.");
    }

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody SignUpRequestDto dto) {
        authService.register(dto);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

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
