package com.reverse.nsu.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "USER_CONSENT")
public class UserConsent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userConsentId;

    @Column(nullable = false, length = 15)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consentItemId", nullable = false)
    private ConsentItem consentItem;

    @Column(nullable = false)
    private Boolean isAgreed;

    private LocalDateTime agreedDate;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime modifiedDate;

    public static UserConsent of(String userId, ConsentItem consentItem, Boolean isAgreed) {
        UserConsent uc = new UserConsent();
        uc.userId = userId;
        uc.consentItem = consentItem;
        uc.isAgreed = isAgreed;
        uc.agreedDate = isAgreed ? LocalDateTime.now() : null;
        return uc;
    }
}
