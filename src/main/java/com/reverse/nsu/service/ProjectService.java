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
    // ❌ projectApplicationRepository 의존성 완전 제거

    /**
     * 1. 프로젝트 게시글 작성 및 일정/멤버 자동 등록
     */
    @Transactional
    public Integer createProject(ProjectRequestDto dto, String currentUserId) {
        // 기본 벨리데이션
        validateProjectDto(dto);

        // 프로젝트 마스터 엔티티 생성 및 저장
        Project project = Project.builder()
                .leaderId(dto.getLeaderId())
                .projectName(dto.getProjectName())
                .leaderName(dto.getLeaderName())
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
                .userId(dto.getLeaderId())
                .memberRole(MemberRole.LEADER)
                .build();
        projectMemberRepository.save(leader);

        // 팀장 포함하여 초기 멤버 카운트 1 세팅
        savedProject.updateMemberCount(1);

        return savedProject.getProjectId();
    }

    /**
     * 2. [수정] 프로젝트 모집 지원서 제출 ➡️ PROJECT_MEMBER 테이블 적재로 변경
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

        // ❌ 실제 DB 명세서에 저장하지 않는 가짜 필드(email, 일자, 개인정보동의 등)의 예외 검증 로직 완전 걷어냄

        // 예외 처리 규칙 2: 해당 프로젝트에 중복 참여 및 중복 지원 방어 (UK_PROJECT_USER 제약 조건 보호)
        if (projectMemberRepository.existsByProjectAndUserId(project, userId)) {
            throw new IllegalStateException("이미 이 프로젝트의 멤버이거나 지원서를 제출하셨습니다.");
        }

        // [변경] 가짜 엔티티 대신 실제 MariaDB 스펙인 PROJECT_MEMBER 엔티티에 MEMBER 권한으로 적재
        ProjectMember applicant = ProjectMember.builder()
                .project(project)
                .userId(userId)
                .memberRole(MemberRole.MEMBER) // 일반 지원자는 MEMBER 역할로 등록
                .build();

        projectMemberRepository.save(applicant);

        // 💡 만약 팀장의 승인 절차 없이 '지원 즉시 인원수 증가' 규칙이라면 아래 주석을 해제하세요.
        // project.updateMemberCount(project.getMemberCount() + 1);
    }

    /**
     * 3. 프로젝트 목록 및 키워드 검색 조회 (비로그인 접근 가능)
     */
    @Transactional(readOnly = true)
    public Page<ProjectResponseDto> getProjectList(String keyword, ProjectStatus status, Pageable pageable) {
        Page<Project> projectPage;

        // 화면 정의서 검색 로직 분기 구현
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

        // 요구사항 결과 반환용 DTO 래핑 후 반환
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

    private void validateProjectDto(ProjectRequestDto dto) {
        if (dto.getProjectName() == null || dto.getProjectName().trim().isEmpty() ||
                dto.getLeaderId() == null || dto.getLeaderName() == null) {
            throw new IllegalArgumentException("필수 항목(프로젝트명, 팀장 정보)이 누락되었습니다.");
        }
    }
}