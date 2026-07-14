package com.reverse.nsu.dto;

import com.reverse.nsu.entity.Users;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class UserListResponseDto {
    private String userId;
    private String userName;
    private String userEmail;
    private String userMbti;
    private Integer roleId;
    private String roleName;
    private LocalDateTime createdDate;

    public UserListResponseDto(Users user) {
        this.userId = user.getUserId();
        this.userName = user.getUserName();
        this.userEmail = user.getUserEmail();
        this.userMbti = user.getUserMbti();
        this.roleId = (user.getRole() != null) ? user.getRole().getRoleId() : null;
        this.roleName = (user.getRole() != null) ? user.getRole().getRoleName() : null;
        this.createdDate = user.getCreatedDate();
    }
}
