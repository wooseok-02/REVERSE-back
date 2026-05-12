package com.reverse.nsu.service;

import com.reverse.nsu.dto.ClubIntroRequestDto;
import com.reverse.nsu.dto.ClubIntroResponseDto;
import com.reverse.nsu.entity.ClubIntro;
import com.reverse.nsu.repository.ClubIntroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClubIntroService {

    private final ClubIntroRepository clubIntroRepository;
    private final R2Service r2Service;

    /**
     * [추가] 메인 페이지용 조회 로직
     * isActive가 true인 데이터만 조회하여 ResponseDto로 변환합니다.
     */
    @Transactional(readOnly = true)
    public List<ClubIntroResponseDto> getActiveClubIntros() {
        return clubIntroRepository.findByIsActiveTrue()
                .stream()
                .map(ClubIntroResponseDto::new)
                .collect(Collectors.toList());
    }

    // 기존 기능: 전체 조회 (관리자용)
    @Transactional(readOnly = true)
    public List<ClubIntro> getAll() {
        return clubIntroRepository.findAll();
    }

    // 기존 기능: 이미지 업로드 → URL 반환
    public String uploadBannerImage(MultipartFile file) throws IOException {
        return r2Service.upload(file, "club");
    }

    // 기존 기능: 데이터 DB 저장
    @Transactional
    public ClubIntro save(ClubIntroRequestDto dto) {
        return clubIntroRepository.save(ClubIntro.from(dto));
    }

    // 기존 기능: DB 삭제 + R2 이미지 삭제
    @Transactional
    public void delete(Integer id) { // ID 타입을 엔티티와 맞춰 Integer로 변경 권장
        ClubIntro entity = clubIntroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ClubIntro not found: " + id));

        if (entity.getBannerUrl() != null) {
            r2Service.delete(entity.getBannerUrl());
        }
        clubIntroRepository.deleteById(id);
    }

    // 기존 기능: 데이터 수정 (이미지 변경 시 기존 R2 이미지 삭제)
    @Transactional
    public ClubIntro update(Integer id, ClubIntroRequestDto dto) {
        ClubIntro entity = clubIntroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ClubIntro not found: " + id));

        String oldBannerUrl = entity.getBannerUrl();
        String newBannerUrl = dto.getBannerUrl();

        // 새로운 URL이 들어왔고 기존과 다르다면 이전 이미지 삭제
        if (oldBannerUrl != null && newBannerUrl != null && !oldBannerUrl.equals(newBannerUrl)) {
            r2Service.delete(oldBannerUrl);
        }

        entity.update(dto);
        return clubIntroRepository.save(entity);
    }
}