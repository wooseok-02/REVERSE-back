package com.reverse.nsu.controller;

import com.reverse.nsu.service.R2Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/r2")
public class R2TestController {

    private final R2Service r2Service;

    public R2TestController(R2Service r2Service) {
        this.r2Service = r2Service;
    }

    /**
     * 이미지 업로드 테스트
     * POST /api/r2/upload?folder=test
     */
    @PostMapping("/upload")
    public ResponseEntity<String> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "test") String folder
    ) throws IOException {
        String url = r2Service.upload(file, folder);
        return ResponseEntity.ok(url);
    }

    /**
     * 이미지 삭제 테스트
     * DELETE /api/r2/delete?fileUrl=https://...
     */
    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestParam("fileUrl") String fileUrl) {
        r2Service.delete(fileUrl);
        return ResponseEntity.ok("삭제 완료: " + fileUrl);
    }
}