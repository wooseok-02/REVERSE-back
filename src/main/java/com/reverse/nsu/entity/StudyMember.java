package com.reverse.nsu.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "STUDY_MEMBER", uniqueConstraints = {
        @UniqueConstraint(name = "UQ_STUDY_MEMBER", columnNames = {"studyId", "userId"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "studyMemberId")
    private Integer studyMemberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studyId", nullable = false)
    private Study study;

    @Column(name = "userId", nullable = false, length = 15)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "memberRole", nullable = false, length = 20)
    private MemberRole memberRole = MemberRole.MEMBER;

    @CreationTimestamp
    @Column(name = "joinedDate", nullable = false, updatable = false)
    private LocalDateTime joinedDate;

    @Builder
    public StudyMember(Study study, String userId, MemberRole memberRole) {
        this.study = study;
        this.userId = userId;
        this.memberRole = memberRole != null ? memberRole : MemberRole.MEMBER;
    }
}
