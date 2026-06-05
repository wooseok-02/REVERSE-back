package com.reverse.nsu.service;

import com.reverse.nsu.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 토큰에서 추출된 userId 기반으로 역할을 DB에서 조회하여 권한을 판단한다.
 *
 * roleId 체계:
 *  1 = SUPER_ADMIN (최고관리자)
 *  2 = ADMIN       (관리자)
 *  3 = MEMBER      (정회원)
 *  4 = ASSOCIATE   (준회원)
 *  5 = GUEST       (게스트)
 */
@Service
@RequiredArgsConstructor
public class RoleCheckService {

    private final UsersRepository usersRepository;

    /** roleId 1(최고관리자) 또는 2(관리자) */
    public boolean isAdmin(String userId) {
        if (userId == null) return false;
        return usersRepository.findById(userId)
                .map(u -> u.getRole() != null && u.getRole().getRoleId() <= 2)
                .orElse(false);
    }

    /** roleId 1(최고관리자)만 */
    public boolean isSuperAdmin(String userId) {
        if (userId == null) return false;
        return usersRepository.findById(userId)
                .map(u -> u.getRole() != null && u.getRole().getRoleId() == 1)
                .orElse(false);
    }

    /** roleId <= 4 (준회원 이상: 준회원, 정회원, 관리자, 최고관리자) */
    public boolean isAssociateOrAbove(String userId) {
        if (userId == null) return false;
        return usersRepository.findById(userId)
                .map(u -> u.getRole() != null && u.getRole().getRoleId() <= 4)
                .orElse(false);
    }
}
