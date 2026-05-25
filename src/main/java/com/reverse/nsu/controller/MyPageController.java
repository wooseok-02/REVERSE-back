package com.reverse.nsu.controller;

import com.reverse.nsu.dto.MyPageResponseDto;
import com.reverse.nsu.dto.MyPageUpdateRequestDto;
import com.reverse.nsu.service.MyPageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    /**
     * 1. 마이페이지 회원 정보 단건 조회
     */
    @GetMapping("/{targetUserId}")
    public ResponseEntity<MyPageResponseDto> getMyPage(
            @PathVariable String targetUserId,
            HttpServletRequest request) {

        // 프로젝트 토큰 필터가 request에 심어놓은 로그인 유저 ID 추출
        String currentUserId = getCurrentUserIdFromRequest(request);

        MyPageResponseDto response = myPageService.getMyPageDetail(targetUserId, currentUserId);
        return ResponseEntity.ok(response);
    }

    /**
     * 2. 한 줄 자기소개 수정
     */
    @PatchMapping("/introduce")
    public ResponseEntity<Map<String, Object>> updateIntroduce(
            @RequestBody MyPageUpdateRequestDto dto,
            HttpServletRequest request) {

        String currentUserId = getCurrentUserIdFromRequest(request);
        Map<String, Object> result = new HashMap<>();

        try {
            myPageService.updateIntroduce(dto, currentUserId);

            result.put("success", true);
            result.put("message", "자기소개가 성공적으로 수정되었습니다.");
            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            // 💡 글자 수 제한 등의 비즈니스 예외 발생 시, 401이 아닌 명확한 400 Bad Request를 뱉도록 제어
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 3. 프로필 사진 수정
     */
    @PostMapping("/photo")
    public ResponseEntity<Map<String, Object>> updateProfilePhoto(
            @RequestBody MyPageUpdateRequestDto dto,
            HttpServletRequest request) {

        String currentUserId = getCurrentUserIdFromRequest(request);
        Map<String, Object> result = new HashMap<>();

        try {
            myPageService.updateProfilePhoto(dto, currentUserId);

            result.put("success", true);
            result.put("message", "프로필 사진이 성공적으로 변경되었습니다.");
            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            // 💡 이미지 주소 누락 등 예외 발생 시 400 Bad Request 반환
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 💡 [공통 메서드] Request에서 로그인한 사용자의 ID를 안전하게 파싱합니다.
     */
    private String getCurrentUserIdFromRequest(HttpServletRequest request) {
        String currentUserId = (String) request.getAttribute("userId");
        if (currentUserId == null) {
            currentUserId = (String) request.getAttribute("currentUserId");
        }


        if (currentUserId == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        return currentUserId;
    }
}