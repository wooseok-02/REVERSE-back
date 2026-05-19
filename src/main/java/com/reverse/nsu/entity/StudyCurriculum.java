package com.reverse.nsu.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Table(name = "STUDY_CURRICULUM", uniqueConstraints = {
        @UniqueConstraint(name = "UQ_STUDY_CURRICULUM_WEEK", columnNames = {"studyId", "week"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyCurriculum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "curriculumId")
    private Integer curriculumId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studyId", nullable = false)
    private Study study;

    @Column(name = "week", nullable = false)
    private Integer week;

    @Column(name = "contents", nullable = false, length = 500)
    private String contents;

    @Builder
    public StudyCurriculum(Study study, Integer week, String contents) {
        this.study = study;
        this.week = week;
        this.contents = contents;
    }
}
