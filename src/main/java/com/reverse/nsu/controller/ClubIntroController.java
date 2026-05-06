package com.reverse.nsu.controller;

import com.reverse.nsu.dto.ClubIntroRequestDto;
import com.reverse.nsu.dto.ClubIntroResponseDto;
import com.reverse.nsu.entity.ClubIntro;
import com.reverse.nsu.service.ClubIntroService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/club-intro")
@RequiredArgsConstructor
public class ClubIntroController {

    private final ClubIntroService clubIntroService;

    /**
     * [추가] 메인 페이지용 공개 API (INTRO01)
     * 활성화된(isActive=true) 동아리 소개 정보만 반환합니다.
     * 이 경로는 Security에서 permitAll() 설정을 권장합니다.
     */
    @GetMapping("/main")
    public ResponseEntity<List<ClubIntroResponseDto>> getMainIntro() {
        return ResponseEntity.ok(clubIntroService.getActiveClubIntros());
    }

    // --- 기존 관리자용 기능 (CUD) ---

    // 전체 조회 (관리자용)
    @GetMapping
    public ResponseEntity<List<ClubIntro>> getAll() {
        return ResponseEntity.ok(clubIntroService.getAll());
    }

    // 1단계: 이미지 업로드 → URL 반환
    @PostMapping("/image")
    public ResponseEntity<String> uploadImage(
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        return ResponseEntity.ok(clubIntroService.uploadBannerImage(file));
    }

    // 2단계: 데이터 저장 (bannerUrl 포함한 전체 데이터)
    @PostMapping
    public ResponseEntity<ClubIntro> save(
            @RequestBody ClubIntroRequestDto dto
    ) {
        return ResponseEntity.ok(clubIntroService.save(dto));
    }

    // 수정용 이미지 업로드 (이미지 변경 시에만 사용)
    @PutMapping("/{id}/image")
    public ResponseEntity<String> updateImage(
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        return ResponseEntity.ok(clubIntroService.uploadBannerImage(file));
    }

    // 데이터 수정 (ID 타입을 DB 스키마와 일치하는 Integer로 변경)
    @PutMapping("/{id}")
    public ResponseEntity<ClubIntro> update(
            @PathVariable Integer id,
            @RequestBody ClubIntroRequestDto dto
    ) {
        return ResponseEntity.ok(clubIntroService.update(id, dto));
    }

    // DB + R2 이미지 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id) {
        clubIntroService.delete(id);
        return ResponseEntity.ok("삭제 완료: " + id);
    }
}