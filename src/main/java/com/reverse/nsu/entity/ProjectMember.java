package com.reverse.nsu.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
// 복합 UNIQUE 제약조건 추가: 한 프로젝트 안에 동일 유저가 두 번 들어가는 것을 방지
@Table(name = "PROJECT_MEMBER", uniqueConstraints = {
        @UniqueConstraint(name = "UK_PROJECT_USER", columnNames = {"projectId", "userId"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "projectMemberId")
    private Integer projectMemberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projectId", nullable = false)
    private Project project;

    @Column(name = "userId", nullable = false, length = 15)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "memberRole", nullable = false, length = 20)
    private MemberRole memberRole = MemberRole.MEMBER;

    @CreationTimestamp
    @Column(name = "joinedDate", nullable = false, updatable = false)
    private LocalDateTime joinedDate;

    @Builder
    public ProjectMember(Project project, String userId, MemberRole memberRole) {
        this.project = project;
        this.userId = userId;
        this.memberRole = memberRole != null ? memberRole : MemberRole.MEMBER;
    }
}