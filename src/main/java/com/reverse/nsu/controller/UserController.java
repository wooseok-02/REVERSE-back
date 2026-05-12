package com.reverse.nsu.controller;

import com.reverse.nsu.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 1. 인증번호 요청
    @PostMapping("/find-password/send-code")
    public ResponseEntity<Map<String, Object>> sendCode(@RequestBody Map<String, String> request) {
        try {
            userService.sendVerificationCode(request.get("userId"), request.get("email"));
            return ResponseEntity.ok(Map.of("message", "인증번호가 발송되었습니다."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", 400,
                    "error", "Bad Request",
                    "message", e.getMessage() // "입력하신 정보와 일치하는 회원이 없습니다." 출력
            ));
        }
    }

    // 2. 인증번호 검증
    @PostMapping("/find-password/verify")
    public ResponseEntity<Map<String, Object>> verify(@RequestBody Map<String, String> request) {
        try {
            userService.verifyCode(request.get("email"), request.get("authCode"));

            // 성공 시
            return ResponseEntity.ok(Map.of("message", "인증에 성공했습니다."));

        } catch (RuntimeException e) {
            // 실패 시 (번호가 없거나 틀렸을 때 각각 서비스에서 던진 메시지가 들어감)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", 400,
                    "error", "Bad Request",
                    "message", e.getMessage()
            ));
        }
    }
  
    // 3. 최종 비밀번호 발급
    @PostMapping("/find-password/issue")
    public ResponseEntity<Map<String, Object>> issue(@RequestBody Map<String, String> request) {
        try {
            userService.issueTempPassword(request.get("userId"), request.get("email"));
            return ResponseEntity.ok(Map.of("message", "임시 비밀번호가 메일로 전송되었습니다."));
        } catch (RuntimeException e) {
            // 인증 안 하고 바로 호출하면 여기서 "이메일 인증이 완료되지 않았습니다."가 나감
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "status", 403,
                    "error", "Forbidden",
                    "message", e.getMessage()
            ));
        }
    }

    // ─── 아이디 찾기 ───────────────────────────────────────────────

    // 1. 인증번호 발송
    @PostMapping("/find-username/send-code")
    public ResponseEntity<Map<String, Object>> sendFindUsernameCode(@RequestBody Map<String, String> request) {
        try {
            userService.sendFindUsernameCode(request.get("userName"), request.get("email"));
            return ResponseEntity.ok(Map.of("message", "인증번호가 발송되었습니다."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", 400,
                    "error", "Bad Request",
                    "message", e.getMessage()
            ));
        }
    }

    // 2. 인증번호 확인 → 아이디 반환
    @PostMapping("/find-username/verify")
    public ResponseEntity<Map<String, Object>> findUsername(@RequestBody Map<String, String> request) {
        try {
            String userId = userService.findUsernameByCode(request.get("email"), request.get("authCode"));
            return ResponseEntity.ok(Map.of(
                    "message", "아이디 찾기에 성공했습니다.",
                    "userId", userId
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", 400,
                    "error", "Bad Request",
                    "message", e.getMessage()
            ));
        }
    }
}