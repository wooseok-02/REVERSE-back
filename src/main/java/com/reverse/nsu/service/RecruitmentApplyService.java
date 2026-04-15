package com.reverse.nsu.service;

import com.reverse.nsu.dto.ApplicationRequestDto;
import com.reverse.nsu.entity.ApplicationApplyField;
import com.reverse.nsu.entity.RecruitmentApplication;
import com.reverse.nsu.entity.RecruitmentNotifyEmail;
import com.reverse.nsu.repository.RecruitmentApplicationRepository;
import com.reverse.nsu.repository.RecruitmentNotifyEmailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class RecruitmentApplyService {
    private final RecruitmentApplicationRepository applicationRepository;
    private final RecruitmentNotifyEmailRepository notifyRepository;

    @Transactional
    public void submitApplication(ApplicationRequestDto dto) {
        // 1. 지원서 기본 정보 저장
        RecruitmentApplication app = new RecruitmentApplication();

        app.setRecruitmentId(dto.getRecruitmentId());
        app.setApplicantName(dto.getApplicantName());
        app.setDepartment(dto.getDepartment());
        app.setStudentNumber(dto.getStudentNumber());
        app.setPhoneNumber(dto.getPhoneNumber());
        app.setGrade(dto.getGrade());
        app.setEmail(dto.getEmail());
        app.setTermsAgreed(dto.getTermsAgreed());
        app.setStatus("PENDING");

        // 2. 다중 선택 지원 분야 처리
        for (String fieldName : dto.getApplyFields()) {
            ApplicationApplyField field = new ApplicationApplyField();
            field.setApplication(app);
            field.setApplyField(fieldName);
            app.getApplyFields().add(field);
        }

        applicationRepository.save(app);
        // 여기서 실제로 JavaMailSender를 이용해 "지원 완료 알림" 메일을 보낼 수 있습니다.
    }

    @Transactional
    public void subscribeNotification(String email) {
        RecruitmentNotifyEmail notify = new RecruitmentNotifyEmail();
        notify.setEmail(email);
        notifyRepository.save(notify);
    }
}