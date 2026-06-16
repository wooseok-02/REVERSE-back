package com.reverse.nsu.service;

import com.reverse.nsu.entity.Role;
import com.reverse.nsu.entity.Users;
import com.reverse.nsu.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserAdminService {

    private final UsersRepository usersRepository;
    private final RoleRepository roleRepository;
    private final UserTokenRepository userTokenRepository;
    private final UserPhotoRepository userPhotoRepository;
    private final UserConsentRepository userConsentRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final StudyMemberRepository studyMemberRepository;
    private final VoteRecordRepository voteRecordRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final VoteRepository voteRepository;
    private final PostRepository postRepository;

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
     * 삭제 순서: 타 엔티티의 자식 레코드 → 본인 소유 엔티티(cascade) → 회원
     *
     * @param targetUserId 탈퇴시킬 대상 userId
     */
    @Transactional
    public void forceWithdraw(String targetUserId) {
        if (!usersRepository.existsById(targetUserId)) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다. ID: " + targetUserId);
        }

        // 1. 다른 사용자의 투표에 참여한 기록 삭제
        voteRecordRepository.deleteAllByUserId(targetUserId);

        // 2. 다른 사용자의 게시글에 누른 좋아요 삭제
        postLikeRepository.deleteAllByUserId(targetUserId);

        // 3. 다른 사용자의 게시글에 작성한 댓글 삭제
        //    대댓글 먼저 삭제 후 원댓글 삭제 (원댓글 삭제 시 cascade로 타 사용자 대댓글도 함께 삭제됨)
        commentRepository.deleteAllByUserIdAndParentIsNotNull(targetUserId);
        commentRepository.deleteAllByUserIdAndParentIsNull(targetUserId);

        // 4. 본인이 생성한 투표 삭제 (cascade → VoteOption → VoteRecord)
        voteRepository.deleteAllByUserId(targetUserId);

        // 5. 본인이 작성한 게시글 삭제 (cascade → PostLike, PostAttached, Comment)
        postRepository.deleteAllByUserId(targetUserId);

        // 6. 프로젝트/스터디 멤버 기록 삭제
        projectMemberRepository.deleteAllByUserId(targetUserId);
        studyMemberRepository.deleteAllByUserId(targetUserId);

        // 7. 토큰, 사진, 동의 기록 삭제
        userTokenRepository.deleteAllByUserId(targetUserId);
        userPhotoRepository.deleteAllByUserId(targetUserId);
        userConsentRepository.deleteAllByUserId(targetUserId);

        // 8. 회원 삭제
        usersRepository.deleteById(targetUserId);
    }
}
