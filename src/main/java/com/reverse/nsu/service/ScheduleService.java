package com.reverse.nsu.service;

import com.reverse.nsu.dto.*;
import com.reverse.nsu.entity.Schedule;
import com.reverse.nsu.entity.ScheduleCategory;
import com.reverse.nsu.repository.ScheduleCategoryRepository;
import com.reverse.nsu.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleCategoryRepository categoryRepository;

    // ───────────────────────────────────────────
    // 카테고리
    // ───────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ScheduleCategoryResponseDto> getAllCategories() {
        return categoryRepository.findAllByOrderBySortOrderAsc().stream()
                .map(ScheduleCategoryResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ScheduleCategoryResponseDto> getVisibleCategories() {
        return categoryRepository.findByIsVisibleTrueOrderBySortOrderAsc().stream()
                .map(ScheduleCategoryResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public ScheduleCategoryResponseDto saveCategory(ScheduleCategoryRequestDto dto) {
        ScheduleCategory saved = categoryRepository.save(dto.toEntity());
        return ScheduleCategoryResponseDto.from(saved);
    }

    @Transactional
    public ScheduleCategoryResponseDto updateCategory(Integer id, ScheduleCategoryRequestDto dto) {
        ScheduleCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다. id=" + id));
        category.update(dto.getCategoryName(), dto.getColorCode(), dto.getSortOrder(), dto.getIsVisible(), dto.getUpdatedBy());
        return ScheduleCategoryResponseDto.from(category);
    }

    @Transactional
    public void deleteCategory(Integer id) {
        ScheduleCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다. id=" + id));
        categoryRepository.delete(category);
    }

    // ───────────────────────────────────────────
    // 일정
    // ───────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> getSchedulesByMonth(int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate firstDay = ym.atDay(1);
        LocalDate lastDay = ym.atEndOfMonth();
        return scheduleRepository.findVisibleByMonth(firstDay, lastDay).stream()
                .map(ScheduleResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> getAllSchedulesByMonth(int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate firstDay = ym.atDay(1);
        LocalDate lastDay = ym.atEndOfMonth();
        return scheduleRepository.findAllByMonth(firstDay, lastDay).stream()
                .map(ScheduleResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ScheduleResponseDto getById(Integer id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다. id=" + id));
        return ScheduleResponseDto.from(schedule);
    }

    @Transactional
    public ScheduleResponseDto save(ScheduleRequestDto dto) {
        ScheduleCategory category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다. id=" + dto.getCategoryId()));

        Schedule schedule = Schedule.builder()
                .category(category)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .isAllDay(dto.getIsAllDay() != null ? dto.getIsAllDay() : true)
                .isVisible(dto.getIsVisible() != null ? dto.getIsVisible() : true)
                .updatedBy(dto.getUpdatedBy())
                .build();

        return ScheduleResponseDto.from(scheduleRepository.save(schedule));
    }

    @Transactional
    public ScheduleResponseDto update(Integer id, ScheduleRequestDto dto) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다. id=" + id));

        ScheduleCategory category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다. id=" + dto.getCategoryId()));

        schedule.update(category, dto.getTitle(), dto.getDescription(),
                dto.getStartDate(), dto.getEndDate(),
                dto.getStartTime(), dto.getEndTime(),
                dto.getIsAllDay(), dto.getIsVisible(), dto.getUpdatedBy());

        return ScheduleResponseDto.from(schedule);
    }

    @Transactional
    public void delete(Integer id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다. id=" + id));
        scheduleRepository.delete(schedule);
    }
}
