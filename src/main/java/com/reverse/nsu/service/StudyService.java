package com.reverse.nsu.service;

import com.reverse.nsu.dto.StudyApplicationResponseDto;
import com.reverse.nsu.dto.StudyApplyRequestDto;
import com.reverse.nsu.dto.StudyRequestDto;
import com.reverse.nsu.dto.StudyResponseDto;
import com.reverse.nsu.entity.*;
import com.reverse.nsu.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository studyRepository;
    private final StudyScheduleRepository studyScheduleRepository;
    private final StudyCurriculumRepository studyCurriculumRepository;
    private final StudyMemberRepository studyMemberRepository;
    private final StudyApplicationRepository studyApplicationRepository;

    @Transactional
    public Integer createStudy(StudyRequestDto dto, String currentUserId) {
        if (dto.getStudyName() == null || dto.getStudyName().isBlank() ||
                dto.getLeaderId() == null || dto.getLeaderName() == null) {
            throw new IllegalArgumentException("필수 항목(스터디명, 팀장 정보)이 누락되었습니다.");
        }

        Study study = Study.builder()
                .leaderId(dto.getLeaderId())
                .studyName(dto.getStudyName())
                .leaderName(dto.getLeaderName())
                .language(dto.getLanguage())
                .techStack(dto.getTechStack())
                .description(dto.getDescription())
                .goal(dto.getGoal())
                .location(dto.getLocation())
                .notice(dto.getNotice())
                .status(dto.getStatus() != null ? dto.getStatus() : StudyStatus.ACTIVE)
                .createdBy(currentUserId)
                .build();

        Study saved = studyRepository.save(study);

        saveSchedules(saved, dto.getSchedules());
        saveCurriculums(saved, dto.getCurriculums());

        StudyMember leader = StudyMember.builder()
                .study(saved)
                .userId(dto.getLeaderId())
                .memberRole(MemberRole.LEADER)
                .build();
        studyMemberRepository.save(leader);
        saved.updateMemberCount(1);

        return saved.getStudyId();
    }

    @Transactional
    public StudyResponseDto updateStudy(Integer studyId, StudyRequestDto dto, String currentUserId, Integer roleId) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("스터디를 찾을 수 없습니다."));

        boolean isSuperAdmin = roleId != null && roleId == 1;
        if (!study.getLeaderId().equals(currentUserId) && !isSuperAdmin) {
            throw new SecurityException("스터디 팀장만 수정할 수 있습니다.");
        }

        study.update(
                dto.getStudyName(),
                dto.getLeaderName(),
                dto.getLanguage(),
                dto.getTechStack(),
                dto.getDescription(),
                dto.getGoal(),
                dto.getLocation(),
                dto.getNotice(),
                dto.getStatus()
        );

        if (dto.getSchedules() != null) {
            studyScheduleRepository.deleteAllByStudy(study);
            saveSchedules(study, dto.getSchedules());
        }

        if (dto.getCurriculums() != null) {
            studyCurriculumRepository.deleteAllByStudy(study);
            saveCurriculums(study, dto.getCurriculums());
        }

        return new StudyResponseDto(studyRepository.save(study));
    }

    @Transactional
    public void deleteStudy(Integer studyId, String currentUserId, Integer roleId) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("스터디를 찾을 수 없습니다."));

        boolean isSuperAdmin = roleId != null && roleId == 1;
        if (!study.getLeaderId().equals(currentUserId) && !isSuperAdmin) {
            throw new SecurityException("스터디 팀장만 삭제할 수 있습니다.");
        }

        studyRepository.delete(study);
    }

    @Transactional(readOnly = true)
    public Page<StudyResponseDto> getStudyList(String keyword, StudyStatus status, Pageable pageable) {
        Page<Study> page;

        if (keyword != null && !keyword.isBlank()) {
            page = status != null
                    ? studyRepository.searchByStatusAndKeyword(status, keyword.trim(), pageable)
                    : studyRepository.searchByKeyword(keyword.trim(), pageable);
        } else if (status != null) {
            page = studyRepository.findAllByStatusOrderByCreatedDateDesc(status, pageable);
        } else {
            page = studyRepository.findAllByOrderByCreatedDateDesc(pageable);
        }

        return page.map(StudyResponseDto::new);
    }

    @Transactional(readOnly = true)
    public StudyResponseDto getStudyDetail(Integer studyId) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("스터디를 찾을 수 없습니다."));
        return new StudyResponseDto(study);
    }

    /**
     * 스터디 신청 (정회원 이상) - 승인 대기 상태로 등록
     */
    @Transactional
    public void applyStudy(Integer studyId, String userId, StudyApplyRequestDto dto) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("스터디를 찾을 수 없습니다."));

        if (study.getStatus() == StudyStatus.CLOSED) {
            throw new IllegalStateException("이미 종료된 스터디입니다.");
        }
        if (studyMemberRepository.existsByStudyAndUserId(study, userId)) {
            throw new IllegalStateException("이미 참여 중인 스터디입니다.");
        }
        if (studyApplicationRepository.existsByStudyAndUserIdAndStatus(study, userId, ApplicationStatus.PENDING)) {
            throw new IllegalStateException("이미 신청하여 승인을 기다리고 있습니다.");
        }
        if (dto.getAvailabilities() == null || dto.getAvailabilities().isEmpty()) {
            throw new IllegalArgumentException("가능한 요일과 시간을 최소 1개 이상 선택해야 합니다.");
        }

        StudyApplication application = StudyApplication.builder()
                .study(study)
                .userId(userId)
                .build();

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
        for (StudyApplyRequestDto.AvailabilityDto a : dto.getAvailabilities()) {
            if (a.getDayOfWeek() == null || a.getAvailableTime() == null) {
                throw new IllegalArgumentException("요일과 시간을 모두 입력해야 합니다.");
            }
            application.addAvailability(StudyApplicationAvailability.builder()
                    .studyApplication(application)
                    .dayOfWeek(a.getDayOfWeek())
                    .availableTime(LocalTime.parse(a.getAvailableTime(), fmt))
                    .build());
        }

        studyApplicationRepository.save(application);
    }

    /**
     * 스터디 신청 목록 조회 (팀장 또는 관리자/최고관리자) - 대기 중인 신청만
     */
    @Transactional(readOnly = true)
    public List<StudyApplicationResponseDto> getApplications(Integer studyId, String currentUserId, Integer roleId) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("스터디를 찾을 수 없습니다."));

        checkApprovalPermission(study, currentUserId, roleId);

        return studyApplicationRepository.findAllByStudyAndStatus(study, ApplicationStatus.PENDING)
                .stream()
                .map(StudyApplicationResponseDto::new)
                .collect(Collectors.toList());
    }

    /**
     * 스터디 신청 승인 (팀장 또는 관리자/최고관리자)
     */
    @Transactional
    public void approveApplication(Integer studyApplicationId, String currentUserId, Integer roleId) {
        StudyApplication application = studyApplicationRepository.findById(studyApplicationId)
                .orElseThrow(() -> new IllegalArgumentException("신청 내역을 찾을 수 없습니다."));

        Study study = application.getStudy();
        checkApprovalPermission(study, currentUserId, roleId);

        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 신청입니다.");
        }

        if (!studyMemberRepository.existsByStudyAndUserId(study, application.getUserId())) {
            StudyMember member = StudyMember.builder()
                    .study(study)
                    .userId(application.getUserId())
                    .memberRole(MemberRole.MEMBER)
                    .build();
            studyMemberRepository.save(member);
            study.updateMemberCount(study.getMemberCount() + 1);
        }

        application.approve(currentUserId);
    }

    /**
     * 스터디 신청 반려 (팀장 또는 관리자/최고관리자)
     */
    @Transactional
    public void rejectApplication(Integer studyApplicationId, String currentUserId, Integer roleId) {
        StudyApplication application = studyApplicationRepository.findById(studyApplicationId)
                .orElseThrow(() -> new IllegalArgumentException("신청 내역을 찾을 수 없습니다."));

        Study study = application.getStudy();
        checkApprovalPermission(study, currentUserId, roleId);

        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 신청입니다.");
        }

        application.reject(currentUserId);
    }

    private void checkApprovalPermission(Study study, String currentUserId, Integer roleId) {
        boolean isAdminOrAbove = roleId != null && roleId <= 2;
        boolean isLeader = study.getLeaderId().equals(currentUserId);
        if (!isLeader && !isAdminOrAbove) {
            throw new SecurityException("스터디 팀장 또는 관리자만 처리할 수 있습니다.");
        }
    }

    private void saveSchedules(Study study, List<StudyRequestDto.ScheduleDto> scheduleDtos) {
        if (scheduleDtos == null || scheduleDtos.isEmpty()) return;
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
        List<StudySchedule> schedules = scheduleDtos.stream()
                .map(s -> StudySchedule.builder()
                        .study(study)
                        .dayOfWeek(s.getDayOfWeek())
                        .meetTime(LocalTime.parse(s.getMeetTime(), fmt))
                        .build())
                .collect(Collectors.toList());
        studyScheduleRepository.saveAll(schedules);
    }

    private void saveCurriculums(Study study, List<StudyRequestDto.CurriculumDto> curriculumDtos) {
        if (curriculumDtos == null || curriculumDtos.isEmpty()) return;
        List<StudyCurriculum> curriculums = curriculumDtos.stream()
                .map(c -> StudyCurriculum.builder()
                        .study(study)
                        .week(c.getWeek())
                        .contents(c.getContents())
                        .build())
                .collect(Collectors.toList());
        studyCurriculumRepository.saveAll(curriculums);
    }
}
