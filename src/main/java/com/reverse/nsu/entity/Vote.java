package com.reverse.nsu.entity;

import com.reverse.nsu.dto.VoteRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "VOTE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer voteId;

    @Column(name = "userId", nullable = false, length = 15)
    private String userId;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "deadline")
    private LocalDateTime deadline;

    @Column(name = "isMultiple", nullable = false)
    private Boolean isMultiple = false;

    @Column(name = "createdDate", nullable = false, updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "modifiedDate")
    private LocalDateTime modifiedDate;

    @OneToMany(mappedBy = "vote", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<VoteOption> options = new ArrayList<>();

    @OneToMany(mappedBy = "vote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VoteRecord> records = new ArrayList<>();

    public static Vote create(VoteRequestDto dto, String userId) {
        Vote vote = new Vote();
        vote.userId = userId;
        vote.title = dto.getTitle();
        vote.content = dto.getContent();
        vote.deadline = dto.getDeadline();
        vote.isMultiple = dto.getIsMultiple() != null ? dto.getIsMultiple() : false;
        vote.createdDate = LocalDateTime.now();
        return vote;
    }

    public void update(VoteRequestDto dto) {
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.deadline = dto.getDeadline();
        if (dto.getIsMultiple() != null) this.isMultiple = dto.getIsMultiple();
        this.modifiedDate = LocalDateTime.now();
    }

    public boolean isClosed() {
        return deadline != null && LocalDateTime.now().isAfter(deadline);
    }
}
