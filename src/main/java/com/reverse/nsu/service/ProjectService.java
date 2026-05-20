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
     * 1. 프로젝트 게시글 작성 및 일정/멤버 자동 등록
     * - [수정 완료] dto 내부의 빈 leaderId 대신 토큰에서 나온 currentUserId를 바인딩 및 검증합니다.
     */
    @Transactional
    public Integer createProject(ProjectRequestDto dto, String currentUserId) {
        // 🔥 [수정] 토큰 유저 ID를 같이 넘겨서 벨리데이션 검사 수행
        validateProjectDto(dto, currentUserId);

        // 프로젝트 마스터 엔티티 생성 및 저장
        Project project = Project.builder()
                .leaderId(currentUserId) // 🔥 [수정] dto 대신 토큰에서 파싱된 유저 ID 장전
                .projectName(dto.getProjectName())
                .leaderName(dto.getLeaderName() != null ? dto.getLeaderName() : "프로젝트 팀장") // 빈 값 방어
                .photoUrl(dto.getPhotoUrl())
                .description(dto.getDescription())
                .goal(dto.getGoal())
                .location(dto.getLocation())
                .notice(dto.getNotice())
                .status(dto.getStatus() != null ? dto.getStatus() : ProjectStatus.ACTIVE)
                .createdBy(currentUserId)
                .build();

        Project savedProject = projectRepository.save(project);

        // 요일 및 시간 일정(Schedule)이 있을 경우 함께 연동 저장
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

        // 개설한 팀장을 프로젝트 초기 멤버(LEADER 권한)로 등록
        ProjectMember leader = ProjectMember.builder()
                .project(savedProject)
                .userId(currentUserId) // 🔥 [수정] 토큰 유저 ID로 팀장 권한 맵핑
                .memberRole(MemberRole.LEADER)
                .build();
        projectMemberRepository.save(leader);

        // 팀장 포함하여 초기 멤버 카운트 1 세팅
        savedProject.updateMemberCount(1);

        return savedProject.getProjectId();
    }

    /**
     * 2. 프로젝트 모집 지원서 제출 ➡️ PROJECT_MEMBER 테이블 적재
     */
    @Transactional
    public void applyProject(Integer projectId, String userId, ProjectApplyRequestDto dto) {
        log.info("============== 현재 지원을 시도하는 userId: [{}] ==============", userId);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다."));

        // 예외 처리 규칙 1: 이미 모집 완료(CLOSED)된 프로젝트인 경우 지원 제한
        if (project.getStatus() == ProjectStatus.CLOSED) {
            throw new IllegalStateException("이미 모집이 완료된 프로젝트입니다.");
        }

        // 예외 처리 규칙 2: 해당 프로젝트에 중복 참여 및 중복 지원 방어 (UK_PROJECT_USER 제약 조건 보호)
        if (projectMemberRepository.existsByProjectAndUserId(project, userId)) {
            throw new IllegalStateException("이미 이 프로젝트의 멤버이거나 지원서를 제출하셨습니다.");
        }

        // 실제 MariaDB 스펙인 PROJECT_MEMBER 엔티티에 MEMBER 권한으로 적재
        ProjectMember applicant = ProjectMember.builder()
                .project(project)
                .userId(userId)
                .memberRole(MemberRole.MEMBER)
                .build();

        projectMemberRepository.save(applicant);
    }

    /**
     * 3. 프로젝트 목록 및 키워드 검색 조회 (비로그인 접근 가능)
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
     * 4. 단건 상세 정보 조회 (팝업 노출용)
     */
    @Transactional(readOnly = true)
    public ProjectResponseDto getProjectDetail(Integer projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("검색 결과가 없습니다."));
        return new ProjectResponseDto(project);
    }

    /**
     * 🔥 [수정] 필수값 검증 부위 개편
     * - 기존 dto.getLeaderId() 검증 대신, 아규먼트로 넘어온 토큰 기반 유저 고유 ID(currentUserId)를 검사합니다.
     */
    private void validateProjectDto(ProjectRequestDto dto, String currentUserId) {
        if (dto.getProjectName() == null || dto.getProjectName().trim().isEmpty() ||
                currentUserId == null || currentUserId.trim().isEmpty()) {
            throw new IllegalArgumentException("필수 항목(프로젝트명, 팀장 정보)이 누락되었습니다.");
        }
    }
}