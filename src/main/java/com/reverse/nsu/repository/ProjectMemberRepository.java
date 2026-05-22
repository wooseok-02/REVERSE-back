package com.reverse.nsu.repository;

import com.reverse.nsu.entity.Project;
import com.reverse.nsu.entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Integer> {

    /**
     * 특정 프로젝트의 소속 팀원 명단 조회
     */
    List<ProjectMember> findAllByProject(Project project);

    /**
     * 해당 유저가 이미 이 프로젝트의 멤버(혹은 팀장)로 등록되어 있는지 확인
     */
    boolean existsByProjectAndUserId(Project project, String userId);

    /**
     * 특정 프로젝트에서 특정 유저 탈퇴/제외 시 활용
     */
    Optional<ProjectMember> findByProjectAndUserId(Project project, String userId);

    /**
     * [보너스 추가] 특정 유저 탈퇴 또는 강퇴 시 즉시 삭제 쿼리 발송
     */
    @Modifying
    @Transactional
    void deleteByProjectAndUserId(Project project, String userId);
}