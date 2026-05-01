package com.reverse.nsu.controller;

import com.reverse.nsu.dto.MeResponseDto;
import com.reverse.nsu.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 내 정보 조회
    @GetMapping("/me")
    public ResponseEntity<MeResponseDto> getMe(
            @RequestHeader("Authorization") String token
    ) {
        return ResponseEntity.ok(userService.getMe(token));
    }
}