package com.reverse.nsu.controller;

import com.reverse.nsu.dto.ClubProjectRequestDto;
import com.reverse.nsu.entity.ClubProject;
import com.reverse.nsu.service.ClubProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/club-project")
@RequiredArgsConstructor
public class ClubProjectController {

    private final ClubProjectService clubProjectService;

    // 전체 조회
    @GetMapping
    public ResponseEntity<List<ClubProject>> getAll() {
        return ResponseEntity.ok(clubProjectService.getAll());
    }

    // 1단계: 이미지 업로드 → URL 반환
    @PostMapping("/image")
    public ResponseEntity<String> uploadImage(
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        return ResponseEntity.ok(clubProjectService.uploadThumbnailImage(file));
    }

    // 2단계: 데이터 저장 (thumbnailUrl 포함한 전체 데이터)
    @PostMapping
    public ResponseEntity<ClubProject> save(
            @RequestBody ClubProjectRequestDto dto
    ) {
        return ResponseEntity.ok(clubProjectService.save(dto));
    }

    // DB + R2 이미지 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        clubProjectService.delete(id);
        return ResponseEntity.ok("삭제 완료: " + id);
    }
}