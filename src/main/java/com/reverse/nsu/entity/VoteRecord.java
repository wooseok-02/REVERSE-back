package com.reverse.nsu.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "VOTE_RECORD",
    uniqueConstraints = @UniqueConstraint(columnNames = {"voteId", "userId", "optionId"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoteRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer recordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voteId", nullable = false)
    private Vote vote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "optionId", nullable = false)
    private VoteOption option;

    @Column(name = "userId", nullable = false, length = 15)
    private String userId;

    @Column(name = "createdDate", nullable = false, updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    public static VoteRecord create(Vote vote, VoteOption option, String userId) {
        VoteRecord record = new VoteRecord();
        record.vote = vote;
        record.option = option;
        record.userId = userId;
        record.createdDate = LocalDateTime.now();
        return record;
    }
}
