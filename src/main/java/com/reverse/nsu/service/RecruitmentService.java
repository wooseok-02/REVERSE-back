package com.reverse.nsu.service;

import com.reverse.nsu.dto.*;
import com.reverse.nsu.entity.*;
import com.reverse.nsu.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecruitmentService {

    private final RecruitmentRepository recruitmentRepository;
    private final RecruitmentPageRepository pageRepository;
    private final RecruitmentInterviewSlotRepository slotRepository;
    private final RecruitmentPageIntroRepository introRepository;
    private final RecruitmentPageFieldCardRepository cardRepository;
    private final RecruitmentPageGalleryRepository galleryRepository;
    private final RecruitmentPageContactRepository contactRepository;
    private final RecruitmentApplicationRepository applicationRepository;
    private final RecruitmentNotifyService notifyService;

    /**
     * 지원서 제출
     */
    @Transactional
    public void submitApplication(ApplicationRequestDto dto) {
        Recruitment recruit = recruitmentRepository.findById(dto.getRecruitmentId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공고입니다."));

        LocalDateTime now = LocalDateTime.now();
        boolean isApplyPeriod = (recruit.getApplyStartDate() != null && recruit.getApplyEndDate() != null) &&
                (now.isAfter(recruit.getApplyStartDate()) && now.isBefore(recruit.getApplyEndDate()));

        if (!isApplyPeriod) {
            throw new IllegalStateException("지금은 신청 기간이 아닙니다.");
        }

        if (applicationRepository.existsByRecruitment_RecruitmentIdAndStudentNumber(
                dto.getRecruitmentId(), dto.getStudentNumber())) {
            throw new IllegalStateException("이미 신청하셨습니다.");
        }

        // [수정 포인트] DTO 필드명 변경 및 DB에 없는 portfolioUrl 제거
        RecruitmentApplication application = RecruitmentApplication.builder()
                .recruitment(recruit)
                .applicantName(dto.getApplicantName())
                .studentNumber(dto.getStudentNumber())
                .department(dto.getDepartment()) // userMajor -> department
                .grade(dto.getGrade())
                .phoneNumber(dto.getPhoneNumber()) // userPhone -> phoneNumber
                .email(dto.getEmail())             // userEmail -> email
                // .portfolioUrl(dto.getPortfolioUrl()) // DB에 없으므로 삭제 (방법 1 적용)
                .termsAgreed(dto.getTermsAgreed() ? 1 : 0) // Boolean -> Integer 변환 필요 시 처리
                .status("PENDING")
                .build();

        applicationRepository.save(application);
        // [수정 포인트] 로그 메시지의 getter 메서드명 수정
        log.info(">>>> [지원서 제출 완료] 성함: {}, 학번: {}", dto.getApplicantName(), dto.getStudentNumber());
    }

    /**
     * 1. 공고 상세 페이지 조회
     */
    @Transactional(readOnly = true)
    public RecruitmentResponseDto getRecruitPage(Integer recruitmentId) {
        Recruitment recruit = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공고입니다."));

        LocalDateTime now = LocalDateTime.now();
        boolean isApplyPeriod = (recruit.getApplyStartDate() != null && recruit.getApplyEndDate() != null) &&
                (now.isAfter(recruit.getApplyStartDate()) && now.isBefore(recruit.getApplyEndDate()));

        RecruitmentPage page = pageRepository.findByRecruitment_RecruitmentId(recruitmentId).orElse(null);

        if (page == null) {
            return RecruitmentResponseDto.builder()
                    .recruitmentId(recruit.getRecruitmentId())
                    .title(recruit.getTitle())
                    .applyStartDate(recruit.getApplyStartDate())
                    .applyEndDate(recruit.getApplyEndDate())
                    .isApplyPeriod(isApplyPeriod)
                    .build();
        }

        Integer pageId = page.getPageId();

        RecruitmentResponseDto.PageDetails pageDetails = RecruitmentResponseDto.PageDetails.builder()
                .year(page.getHeroYear())
                .title(page.getHeroTitle())
                .subtitle(page.getHeroSubTitle())
                .heroBgUrl(page.getHeroBgUrl())
                .heroBtnText(isApplyPeriod ? "신청하기" : "지금은 신청 기간이 아닙니다")
                .intros(introRepository.findAllByRecruitmentPage_PageIdOrderBySortOrderAsc(pageId).stream()
                        .map(i -> RecruitmentResponseDto.IntroDetails.builder()
                                .contents(i.getContents())
                                .sortOrder(i.getSortOrder())
                                .build()).collect(Collectors.toList()))
                .cards(cardRepository.findAllByRecruitmentPage_PageIdOrderBySortOrderAsc(pageId).stream()
                        .map(c -> RecruitmentResponseDto.CardDetails.builder()
                                .applyField(c.getApplyField())
                                .title(c.getCardTitle())
                                .subTitle(c.getCardSubTitle())
                                .desc(c.getCardDesc())
                                .imageUrl(c.getImageUrl())
                                .build()).collect(Collectors.toList()))
                .galleries(galleryRepository.findAllByRecruitmentPage_PageIdOrderBySortOrderAsc(pageId).stream()
                        .map(g -> RecruitmentResponseDto.GalleryDetails.builder()
                                .imageUrl(g.getImageUrl())
                                .imageDesc(g.getImageDesc())
                                .tag(g.getTag())
                                .build()).collect(Collectors.toList()))
                .contacts(contactRepository.findAllByRecruitmentPage_PageIdOrderBySortOrderAsc(pageId).stream()
                        .map(ct -> RecruitmentResponseDto.ContactDetails.builder()
                                .type(ct.getContactType())
                                .label(ct.getLabel())
                                .value(ct.getValue())
                                .subValue(ct.getSubValue())
                                .build()).collect(Collectors.toList()))
                .interviewSlots(slotRepository.findAllByRecruitment_RecruitmentId(recruitmentId).stream()
                        .map(s -> RecruitmentResponseDto.SlotDetails.builder()
                                .slotId(s.getSlotId())
                                .date(s.getInterviewDate().toString())
                                .time(s.getStartTime() != null ? s.getStartTime().toString() : "시간 미정")
                                .isAvailable(s.getIsActive())
                                .build()).collect(Collectors.toList()))
                .build();

        return RecruitmentResponseDto.builder()
                .recruitmentId(recruit.getRecruitmentId())
                .title(recruit.getTitle())
                .applyStartDate(recruit.getApplyStartDate())
                .applyEndDate(recruit.getApplyEndDate())
                .isApplyPeriod(isApplyPeriod)
                .page(pageDetails)
                .build();
    }

    // ... (이하 동일한 로직은 유지하되 빌더/메서드 호출 시 필드명 주의)

    @Transactional(readOnly = true)
    public List<RecruitmentResponseDto> getAll() {
        return recruitmentRepository.findAll().stream()
                .map(r -> RecruitmentResponseDto.builder()
                        .recruitmentId(r.getRecruitmentId())
                        .title(r.getTitle())
                        .applyStartDate(r.getApplyStartDate())
                        .applyEndDate(r.getApplyEndDate())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public RecruitmentResponseDto save(RecruitmentRequestDto dto, String adminId) {
        Recruitment recruitment = dto.toEntity();
        recruitment.setUpdatedBy(adminId);
        recruitment.setIsActive(true);

        Recruitment savedRecruit = recruitmentRepository.save(recruitment);

        if (notifyService != null) {
            notifyService.notifySubscribers(savedRecruit.getTitle());
        }
        log.info(">>>> [공고 생성] 제목: {}, 작성자: {}", savedRecruit.getTitle(), adminId);

        return RecruitmentResponseDto.builder()
                .recruitmentId(savedRecruit.getRecruitmentId())
                .title(savedRecruit.getTitle())
                .applyStartDate(savedRecruit.getApplyStartDate())
                .applyEndDate(savedRecruit.getApplyEndDate())
                .build();
    }

    @Transactional
    public void delete(Integer id) {
        Recruitment recruitment = recruitmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공고입니다. ID: " + id));
        recruitmentRepository.delete(recruitment);
        log.info(">>>> [공고 삭제 완료] ID: {}", id);
    }

    @Transactional
    public void createOrUpdatePage(Integer recruitId, RecruitmentResponseDto.PageDetails dto, String adminId) {
        Recruitment recruit = recruitmentRepository.findById(recruitId)
                .orElseThrow(() -> new IllegalArgumentException("공고를 찾을 수 없습니다."));

        RecruitmentPage page = pageRepository.findByRecruitment_RecruitmentId(recruitId)
                .orElseGet(() -> RecruitmentPage.builder().recruitment(recruit).build());

        page.setHeroYear(dto.getYear());
        page.setHeroTitle(dto.getTitle());
        page.setHeroSubTitle(dto.getSubtitle());
        page.setHeroBgUrl(dto.getHeroBgUrl());
        page.setUpdatedBy(adminId);
        page.setIsActive(true);

        RecruitmentPage savedPage = pageRepository.save(page);

        clearSubData(savedPage.getPageId());

        if (dto.getIntros() != null) {
            introRepository.saveAll(dto.getIntros().stream()
                    .map(i -> RecruitmentPageIntro.builder()
                            .recruitmentPage(savedPage)
                            .contents(i.getContents())
                            .sortOrder(i.getSortOrder())
                            .updatedBy(adminId)
                            .build()).collect(Collectors.toList()));
        }

        if (dto.getCards() != null) {
            cardRepository.saveAll(dto.getCards().stream()
                    .map(c -> RecruitmentPageFieldCard.builder()
                            .recruitmentPage(savedPage)
                            .applyField(c.getApplyField())
                            .cardTitle(c.getTitle())
                            .cardSubTitle(c.getSubTitle())
                            .cardDesc(c.getDesc())
                            .imageUrl(c.getImageUrl())
                            .updatedBy(adminId)
                            .build()).collect(Collectors.toList()));
        }

        if (dto.getGalleries() != null) {
            galleryRepository.saveAll(dto.getGalleries().stream()
                    .map(g -> RecruitmentPageGallery.builder()
                            .recruitmentPage(savedPage)
                            .imageUrl(g.getImageUrl())
                            .imageDesc(g.getImageDesc())
                            .tag(g.getTag())
                            .updatedBy(adminId)
                            .build()).collect(Collectors.toList()));
        }

        if (dto.getContacts() != null) {
            contactRepository.saveAll(dto.getContacts().stream()
                    .map(ct -> RecruitmentPageContact.builder()
                            .recruitmentPage(savedPage)
                            .contactType(ct.getType())
                            .label(ct.getLabel())
                            .value(ct.getValue())
                            .subValue(ct.getSubValue())
                            .updatedBy(adminId)
                            .build()).collect(Collectors.toList()));
        }
    }

    private void clearSubData(Integer pageId) {
        introRepository.deleteByRecruitmentPage_PageId(pageId);
        cardRepository.deleteByRecruitmentPage_PageId(pageId);
        galleryRepository.deleteByRecruitmentPage_PageId(pageId);
        contactRepository.deleteByRecruitmentPage_PageId(pageId);
    }

    @Transactional(readOnly = true)
    public boolean checkApplyPeriod(Integer recruitmentId) {
        Recruitment recruit = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공고입니다."));
        LocalDateTime now = LocalDateTime.now();
        return (recruit.getApplyStartDate() != null && recruit.getApplyEndDate() != null) &&
                (now.isAfter(recruit.getApplyStartDate()) && now.isBefore(recruit.getApplyEndDate()));
    }

    @Transactional(readOnly = true)
    public Integer getPageIdByRecruitmentId(Integer recruitmentId) {
        return recruitmentRepository.findById(recruitmentId)
                .map(recruit -> {
                    if (recruit.getRecruitmentPage() == null) {
                        throw new IllegalArgumentException("해당 공고에 연결된 상세 페이지가 없습니다.");
                    }
                    return recruit.getRecruitmentPage().getPageId();
                })
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공고입니다. ID: " + recruitmentId));
    }

    @Transactional(readOnly = true)
    public List<RecruitmentResponseDto.GalleryDetails> getGalleriesByTag(Integer pageId, String tag) {
        List<RecruitmentPageGallery> galleries;
        if (tag == null || tag.trim().isEmpty() || tag.equalsIgnoreCase("all")) {
            galleries = galleryRepository.findAllByRecruitmentPage_PageIdOrderBySortOrderAsc(pageId);
        } else {
            galleries = galleryRepository.findAllByRecruitmentPage_PageIdAndTagOrderBySortOrderAsc(pageId, tag);
        }
        return galleries.stream()
                .map(g -> RecruitmentResponseDto.GalleryDetails.builder()
                        .imageUrl(g.getImageUrl())
                        .imageDesc(g.getImageDesc())
                        .tag(g.getTag())
                        .build())
                .collect(Collectors.toList());
    }
}