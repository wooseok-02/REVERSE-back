package com.reverse.nsu.service;

import com.reverse.nsu.entity.Role;
import com.reverse.nsu.entity.Users;
import com.reverse.nsu.repository.RoleRepository;
import com.reverse.nsu.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserAdminService {

    private final UsersRepository usersRepository;
    private final RoleRepository roleRepository;

    /**
     * 회원 권한 수정 (최고관리자 전용)
     *
     * @param targetUserId 권한을 변경할 대상 userId
     * @param newRoleId    변경할 roleId (1~5)
     */
    @Transactional
    public void updateUserRole(String targetUserId, Integer newRoleId) {
        Users target = usersRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. ID: " + targetUserId));

        Role newRole = roleRepository.findById(newRoleId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 권한입니다. roleId: " + newRoleId));

        target.setRole(newRole);
    }

    /**
     * 회원 강제 탈퇴 (최고관리자 전용)
     *
     * @param targetUserId 탈퇴시킬 대상 userId
     */
    @Transactional
    public void forceWithdraw(String targetUserId) {
        if (!usersRepository.existsById(targetUserId)) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다. ID: " + targetUserId);
        }
        usersRepository.deleteById(targetUserId);
    }
}
