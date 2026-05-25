package com.reverse.nsu.service;

import com.reverse.nsu.dto.MyPageResponseDto;
import com.reverse.nsu.dto.MyPageUpdateRequestDto;
import com.reverse.nsu.entity.UserPhoto;
import com.reverse.nsu.entity.Users;
import com.reverse.nsu.repository.UserPhotoRepository;
import com.reverse.nsu.repository.UsersRepository; // 기존 프로젝트의 레포지토리 이름에 맞추어 임포트하세요.
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final UsersRepository usersRepository;
    private final UserPhotoRepository userPhotoRepository;

    /**
     * 1. 마이페이지 회원 정보 조회 (본인/타인 공통)
     */
    @Transactional(readOnly = true)
    public MyPageResponseDto getMyPageDetail(String targetUserId, String currentUserId) {
        // 조회 대상 유저가 존재하는지 검증
        Users targetUser = usersRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 유저의 프로필 사진 정보 조회 (없을 수도 있으므로 Optional 처리)
        UserPhoto userPhoto = userPhotoRepository.findByUserId(targetUserId).orElse(null);

        // 🔒 현재 로그인한 유저(토큰 추출)와 조회 대상 유저가 일치하는지 비교하여 본인 여부 판별
        boolean isOwner = targetUser.getUserId().equals(currentUserId);

        return new MyPageResponseDto(targetUser, userPhoto, isOwner);
    }

    /**
     * 2. 한 줄 자기소개 수정
     */
    @Transactional
    public void updateIntroduce(MyPageUpdateRequestDto dto, String currentUserId) {
        Users user = usersRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));

        // 자기소개 최대 글자 수 글자 수 제한 방어 (화면정의서 요구사항 공지반영)
        if (dto.getUserIntroduce() != null && dto.getUserIntroduce().length() > 100) {
            throw new IllegalArgumentException("자기소개는 최대 100자까지 입력 가능합니다.");
        }

        user.updateIntroduce(dto.getUserIntroduce());
    }

    /**
     * 3. 프로필 사진 수정 (핵심: Upsert 로직)
     */
    @Transactional
    public void updateProfilePhoto(MyPageUpdateRequestDto dto, String currentUserId) {
        Users user = usersRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));

        if (dto.getAttachedUrl() == null || dto.getAttachedUrl().trim().isEmpty()) {
            throw new IllegalArgumentException("유효하지 않은 이미지 경로입니다.");
        }

        // DB에 이미 해당 유저의 사진 레코드가 있는지 수색
        UserPhoto existingPhoto = userPhotoRepository.findByUserId(currentUserId).orElse(null);

        if (existingPhoto != null) {
            // 🔥 Case A: 이미 있으면 파일명, URL, 용량을 새 정보로 '수정(Update)'
            existingPhoto.updatePhotoDetails(
                    dto.getAttachedName(),
                    dto.getAttachedUrl(),
                    dto.getAttachedSize()
            );
        } else {
            // 🔥 Case B: 한 번도 사진을 올린 적 없는 유저라면 새 레코드 '생성 및 삽입(Insert)'
            UserPhoto newPhoto = UserPhoto.builder()
                    .userId(currentUserId)
                    .attachedName(dto.getAttachedName())
                    .attachedUrl(dto.getAttachedUrl())
                    .attachedSize(dto.getAttachedSize())
                    .build();
            userPhotoRepository.save(newPhoto);
        }
    }
}