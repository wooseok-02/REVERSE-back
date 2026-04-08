package com.reverse.nsu.service;

import com.reverse.nsu.dto.ClubProjectRequestDto;
import com.reverse.nsu.entity.ClubProject;
import com.reverse.nsu.repository.ClubProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClubProjectService {

    private final ClubProjectRepository clubProjectRepository;
    private final R2Service r2Service;

    // 노출 순서대로 전체 조회
    public List<ClubProject> getAll() {
        return clubProjectRepository.findAllByOrderBySortOrderAsc();
    }

    // 1단계: 이미지 업로드 → URL 반환
    public String uploadThumbnailImage(MultipartFile file) throws IOException {
        return r2Service.upload(file, "project");
    }

    // 2단계: 데이터 DB 저장 (URL 포함)
    public ClubProject save(ClubProjectRequestDto dto) {
        return clubProjectRepository.save(ClubProject.from(dto));
    }

    // DB 삭제 + R2 이미지 삭제
    public void delete(Long id) {
        ClubProject entity = clubProjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ClubProject not found: " + id));
        if (entity.getThumbnailUrl() != null) {
            r2Service.delete(entity.getThumbnailUrl());
        }
        clubProjectRepository.deleteById(id);
    }
}