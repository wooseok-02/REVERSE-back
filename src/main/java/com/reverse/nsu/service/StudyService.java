package com.reverse.nsu.service;

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
    public StudyResponseDto updateStudy(Integer studyId, StudyRequestDto dto, String currentUserId) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("스터디를 찾을 수 없습니다."));

        if (!study.getLeaderId().equals(currentUserId)) {
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
    public void deleteStudy(Integer studyId, String currentUserId) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("스터디를 찾을 수 없습니다."));

        if (!study.getLeaderId().equals(currentUserId)) {
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
