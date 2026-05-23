package com.reverse.nsu.service;

import com.reverse.nsu.dto.*;
import com.reverse.nsu.entity.*;
import com.reverse.nsu.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectScheduleRepository projectScheduleRepository;
    private final ProjectMemberRepository projectMemberRepository;

    /**
     * 1. [C] 프로젝트 게시글 작성
     */
    @Transactional
    public Integer createProject(ProjectRequestDto dto, String currentUserId) {
        validateProjectDto(dto, currentUserId);

        Project project = Project.builder()
                .leaderId(currentUserId)
                .projectName(dto.getProjectName())
                .leaderName(dto.getLeaderName() != null ? dto.getLeaderName() : "프로젝트 팀장")
                .photoUrl(dto.getPhotoUrl())
                .description(dto.getDescription())
                .goal(dto.getGoal())
                .location(dto.getLocation())
                .notice(dto.getNotice())
                .status(dto.getStatus() != null ? dto.getStatus() : ProjectStatus.ACTIVE)
                .createdBy(currentUserId)
                .build();

        Project savedProject = projectRepository.save(project);

        if (dto.getSchedules() != null && !dto.getSchedules().isEmpty()) {
            List<ProjectSchedule> schedules = dto.getSchedules().stream()
                    .map(sDto -> ProjectSchedule.builder()
                            .project(savedProject)
                            .dayOfWeek(sDto.getDayOfWeek())
                            .meetTime(LocalTime.parse(sDto.getMeetTime(), DateTimeFormatter.ofPattern("HH:mm")))
                            .build())
                    .collect(Collectors.toList());
            projectScheduleRepository.saveAll(schedules);
        }

        ProjectMember leader = ProjectMember.builder()
                .project(savedProject)
                .userId(currentUserId)
                .memberRole(MemberRole.LEADER)
                .build();
        projectMemberRepository.save(leader);

        savedProject.updateMemberCount(1);

        return savedProject.getProjectId();
    }

    /**
     * 2. [U] 프로젝트 게시글 수정
     */
    @Transactional
    public void updateProject(Integer projectId, ProjectRequestDto dto, String currentUserId, Integer roleId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다."));

        // 🔒 작성자 본인이거나, 슈퍼관리자(1)이거나, 일반관리자(2)인 경우 패스
        boolean isOwner = project.getCreatedBy().equals(currentUserId);
        boolean isAdmin = (roleId != null && (roleId == 1 || roleId == 2));

        if (!isOwner && !isAdmin) {
            throw new IllegalStateException("해당 프로젝트를 수정할 권한이 없습니다.");
        }

        // 🚨 예외 처리: 수정 시 프로젝트 이름 필수 입력 검증
        if (dto.getProjectName() == null || dto.getProjectName().trim().isEmpty()) {
            throw new IllegalArgumentException("프로젝트 이름은 필수 입력 사항입니다.");
        }

        project.updateProjectDetails(dto);

        if (dto.getSchedules() != null) {
            // 1. 기존 연관 스케줄 벌크 삭제 요청
            projectScheduleRepository.deleteAllByProject(project);

            // 🔥 [수정 포인트] 영속성 컨텍스트의 삭제 쿼리를 DB에 즉시 동기화(Flush)
            // 이를 통해 UQ_PROJECT_SCHEDULE(projectId-dayOfWeek) 유니크 키 중복 충돌 예외를 완전히 예방합니다.
            projectScheduleRepository.flush();

            // 2. 새 스케줄 데이터 빌드 및 일괄 저장
            List<ProjectSchedule> newSchedules = dto.getSchedules().stream()
                    .map(sDto -> ProjectSchedule.builder()
                            .project(project)
                            .dayOfWeek(sDto.getDayOfWeek())
                            .meetTime(LocalTime.parse(sDto.getMeetTime(), DateTimeFormatter.ofPattern("HH:mm")))
                            .build())
                    .collect(Collectors.toList());
            projectScheduleRepository.saveAll(newSchedules);
        }
    }

    /**
     * 3. [D] 프로젝트 게시글 삭제
     */
    @Transactional
    public void deleteProject(Integer projectId, String currentUserId, Integer roleId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다."));

        boolean isOwner = project.getCreatedBy().equals(currentUserId);
        boolean isAdmin = (roleId != null && (roleId == 1 || roleId == 2));

        if (!isOwner && !isAdmin) {
            throw new IllegalStateException("해당 프로젝트를 삭제할 권한이 없습니다.");
        }

        projectRepository.delete(project);
    }

    /**
     * 4. 프로젝트 모집 지원서 제출
     */
    @Transactional
    public void applyProject(Integer projectId, String userId, ProjectApplyRequestDto dto) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다."));

        if (project.getStatus() == ProjectStatus.CLOSED) {
            throw new IllegalStateException("이미 모집이 완료된 프로젝트입니다.");
        }

        if (projectMemberRepository.existsByProjectAndUserId(project, userId)) {
            throw new IllegalStateException("이미 이 프로젝트의 멤버이거나 지원서를 제출하셨습니다.");
        }

        ProjectMember applicant = ProjectMember.builder()
                .project(project)
                .userId(userId)
                .memberRole(MemberRole.MEMBER)
                .build();

        projectMemberRepository.save(applicant);
    }

    /**
     * 5. [R] 프로젝트 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<ProjectResponseDto> getProjectList(String keyword, ProjectStatus status, Pageable pageable) {
        Page<Project> projectPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            if (status != null) {
                projectPage = projectRepository.searchByStatusAndKeyword(status, keyword.trim(), pageable);
            } else {
                projectPage = projectRepository.searchByKeyword(keyword.trim(), pageable);
            }
        } else if (status != null) {
            projectPage = projectRepository.findAllByStatusOrderByCreatedDateDesc(status, pageable);
        } else {
            projectPage = projectRepository.findAllByOrderByCreatedDateDesc(pageable);
        }

        return projectPage.map(ProjectResponseDto::new);
    }

    /**
     * 6. 프로젝트 단건 상세 조회
     */
    @Transactional(readOnly = true)
    public ProjectResponseDto getProjectDetail(Integer projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("검색 결과가 없습니다."));
        return new ProjectResponseDto(project);
    }

    private void validateProjectDto(ProjectRequestDto dto, String currentUserId) {
        if (dto.getProjectName() == null || dto.getProjectName().trim().isEmpty() ||
                currentUserId == null || currentUserId.trim().isEmpty()) {
            throw new IllegalArgumentException("필수 항목(프로젝트명, 팀장 정보)이 누락되었습니다.");
        }
    }
}