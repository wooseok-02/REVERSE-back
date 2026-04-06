package com.reverse.nsu.service;

import com.reverse.nsu.dto.ClubIntroRequestDto;
import com.reverse.nsu.entity.ClubIntro;
import com.reverse.nsu.repository.ClubIntroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClubIntroService {

    private final ClubIntroRepository clubIntroRepository;
    private final R2Service r2Service;

    // 전체 조회
    public List<ClubIntro> getAll() {
        return clubIntroRepository.findAll();
    }

    // 1단계: 이미지 업로드 → URL 반환
    public String uploadBannerImage(MultipartFile file) throws IOException {
        return r2Service.upload(file, "club");
    }

    // 2단계: 데이터 DB 저장 (URL 포함)
    public ClubIntro save(ClubIntroRequestDto dto) {
        return clubIntroRepository.save(ClubIntro.from(dto));
    }

        // DB 삭제 + R2 이미지 삭제
    public void delete(Long id) {
        ClubIntro entity = clubIntroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ClubIntro not found: " + id));
        if (entity.getBannerUrl() != null) {
            r2Service.delete(entity.getBannerUrl());
        }
        clubIntroRepository.deleteById(id);
    }
}