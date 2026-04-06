package com.reverse.nsu.controller;

import com.reverse.nsu.dto.OfficerRequestDto;
import com.reverse.nsu.entity.Officer;
import com.reverse.nsu.service.OfficerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/officer")
@RequiredArgsConstructor
public class OfficerController {

    private final OfficerService officerService;

    // 전체 조회
    @GetMapping
    public ResponseEntity<List<Officer>> getAll() {
        return ResponseEntity.ok(officerService.getAll());
    }

    // 1단계: 이미지 업로드 → URL 반환
    @PostMapping("/image")
    public ResponseEntity<String> uploadImage(
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        return ResponseEntity.ok(officerService.uploadPhotoImage(file));
    }

    // 2단계: 데이터 저장 (photoUrl 포함한 전체 데이터)
    @PostMapping
    public ResponseEntity<Officer> save(
            @RequestBody OfficerRequestDto dto
    ) {
        return ResponseEntity.ok(officerService.save(dto));
    }

     // DB + R2 이미지 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        officerService.delete(id);
        return ResponseEntity.ok("삭제 완료: " + id);
    }
}