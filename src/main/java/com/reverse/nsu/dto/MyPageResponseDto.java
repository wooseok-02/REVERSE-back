package com.reverse.nsu.dto;

import com.reverse.nsu.entity.Users;
import com.reverse.nsu.entity.UserPhoto;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MyPageResponseDto {
    private String userId;
    private String userName;
    private String userEmail;
    private String userMbti;
    private String userIntroduce;
    private String userPhotoUrl; // 💡 USER_PHOTO 테이블에서 가져올 프로필 주소
    private String roleName;     // 💡 Role 엔티티에서 가져올 등급 이름 (부원, 임원 등)
    private boolean isOwner;     // 💡 본인 마이페이지 접근 여부 (수정 UI 제어용)

    public MyPageResponseDto(Users user, UserPhoto userPhoto, boolean isOwner) {
        this.userId = user.getUserId();
        this.userName = user.getUserName();
        this.userEmail = user.getUserEmail();
        this.userMbti = user.getUserMbti();
        this.userIntroduce = user.getUserIntroduce();
        this.roleName = (user.getRole() != null) ? user.getRole().getRoleName() : "일반회원";

        // 프로필 이미지가 등록되어 있으면 R2 URL을, 없으면 프론트가 처리할 기본 기본값(null) 세팅
        this.userPhotoUrl = (userPhoto != null) ? userPhoto.getAttachedUrl() : null;
        this.isOwner = isOwner;
    }
}