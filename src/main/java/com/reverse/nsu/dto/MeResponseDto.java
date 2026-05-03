package com.reverse.nsu.dto;

import com.reverse.nsu.entity.Users;
import lombok.Getter;

@Getter
public class MeResponseDto {
    private String userId;
    private String userName;
    private String roleName;
    private String userIntroduce;
    private String userMbti;

    public static MeResponseDto from(Users user) {
        MeResponseDto dto = new MeResponseDto();
        dto.userId = user.getUserId();
        dto.userName = user.getUserName();
        dto.roleName = user.getRole().getRoleName();
        dto.userIntroduce = user.getUserIntroduce();
        dto.userMbti = user.getUserMbti();
        return dto;
    }
}