package com.reverse.nsu.service;

import com.reverse.nsu.dto.RecruitmentRequestDto;
import com.reverse.nsu.entity.*;
import com.reverse.nsu.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecruitmentApplyService {
    private final RecruitmentApplicationRepository applicationRepository;

    @Transactional
    public void submitApplication(RecruitmentRequestDto dto) {
        RecruitmentApplication app = new RecruitmentApplication();
        app.setRecruitmentId(dto.getRecruitmentId());
        app.setUserName(dto.getUserName());
        app.setUserMajor(dto.getUserMajor());
        app.setUserPhone(dto.getUserPhone());
        app.setUserEmail(dto.getUserEmail());
        app.setPortfolioUrl(dto.getPortfolioUrl());

        // 면접 슬롯 저장 로직 (수정된 사항)
        dto.getSelectedSlotIds().forEach(slotId -> {
            ApplicationInterviewSchedule schedule = new ApplicationInterviewSchedule();
            schedule.setSlotId(slotId);
            app.getApplicationInterviewSchedule().add(schedule);
        });

        applicationRepository.save(app);
    }
}