package com.reverse.nsu.repository;

import com.reverse.nsu.entity.Project;
import com.reverse.nsu.entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
     * - [활용] ProjectService.applyProject에서 중복 지원 및 중복 가입 방어 예외 처리에 사용됩니다.
     */
    boolean existsByProjectAndUserId(Project project, String userId);

    /**
     * 특정 프로젝트에서 특정 유저 탈퇴/제외 시 활용
     */
    Optional<ProjectMember> findByProjectAndUserId(Project project, String userId);
}