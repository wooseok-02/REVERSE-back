package com.reverse.nsu.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "RECRUITMENT_PAGE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@AttributeOverrides({
        @AttributeOverride(name = "createdDate", column = @Column(name = "createdDate")),
        @AttributeOverride(name = "modifiedDate", column = @Column(name = "modifiedDate"))
})
public class RecruitmentPage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pageId")
    private Integer pageId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitmentId", nullable = false)
    private Recruitment recruitment;

    @Column(name = "heroYear")
    private String heroYear;

    @Column(name = "heroTitle")
    private String heroTitle;

    @Column(name = "heroSubTitle")
    private String heroSubTitle;

    @Column(name = "heroBtnText")
    private String heroBtnText;

    @Column(name = "heroBgUrl")
    private String heroBgUrl;

    @Column(name = "isActive")
    private Boolean isActive;

    @Column(name = "updatedBy")
    private String updatedBy;

    // [중요] 내부에서 직접 선언했던 createdDate, modifiedDate 필드를 삭제했습니다.
    // 대신 상단의 @AttributeOverrides가 부모의 필드를 DB 컬럼 'createdDate', 'modifiedDate'에 매핑합니다.

    @Builder.Default
    @OneToMany(mappedBy = "recruitmentPage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecruitmentPageIntro> intros = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "recruitmentPage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecruitmentPageFieldCard> cards = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "recruitmentPage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecruitmentPageGallery> galleries = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "recruitmentPage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecruitmentPageContact> contacts = new ArrayList<>();
}