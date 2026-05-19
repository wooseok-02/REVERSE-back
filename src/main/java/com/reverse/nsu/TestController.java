package com.reverse.nsu;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TestController {

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        String html = """
                    <!DOCTYPE html>
                    <html lang="kr">
                    <meta charset="UTF-8">
                    <head>
                        <title>서버 상태</title>
                    </head>
                    <body>
                        <h1>서버 작동중 </h1>
                    </body>
                    </html>
                    """;

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }

    @GetMapping("/api/deploy-check")
    public ResponseEntity<Map<String, String>> deployCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "ok",
                "version", "deploy-check-2026-05-19-01"
        ));
    }
}
