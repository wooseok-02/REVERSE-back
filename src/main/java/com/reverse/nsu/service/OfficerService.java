package com.reverse.nsu.service;

import com.reverse.nsu.dto.OfficerRequestDto;
import com.reverse.nsu.entity.Officer;
import com.reverse.nsu.repository.OfficerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OfficerService {

    private final OfficerRepository officerRepository;
    private final R2Service r2Service;

    // 노출 여부 true, 기수 내림차순, 순서대로 조회
    public List<Officer> getAll() {
        return officerRepository.findAllByIsVisibleTrueOrderByGenerationDescSortOrderAsc();
    }

    // 1단계: 이미지 업로드 → URL 반환
    public String uploadPhotoImage(MultipartFile file) throws IOException {
        return r2Service.upload(file, "executive");
    }

    // 2단계: 데이터 DB 저장 (URL 포함)
    public Officer save(OfficerRequestDto dto) {
        return officerRepository.save(Officer.from(dto));
    }

    // DB 삭제 + R2 이미지 삭제
    public void delete(Long id) {
        Officer entity = officerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Officer not found: " + id));
        if (entity.getPhotoUrl() != null) {
            r2Service.delete(entity.getPhotoUrl());
        }
        officerRepository.deleteById(id);
    }
}