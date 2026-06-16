package com.reverse.nsu.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "VOTE_OPTION")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoteOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer optionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voteId", nullable = false)
    private Vote vote;

    @Column(name = "optionText", nullable = false, length = 100)
    private String optionText;

    @Column(name = "sortOrder", nullable = false)
    private Integer sortOrder = 0;

    @Column(name = "voteCount", nullable = false)
    private Integer voteCount = 0;

    @OneToMany(mappedBy = "option", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VoteRecord> records = new ArrayList<>();

    public static VoteOption create(Vote vote, String optionText, int sortOrder) {
        VoteOption option = new VoteOption();
        option.vote = vote;
        option.optionText = optionText;
        option.sortOrder = sortOrder;
        option.voteCount = 0;
        return option;
    }

    public void incrementCount() { this.voteCount++; }
    public void decrementCount() { if (this.voteCount > 0) this.voteCount--; }
}
