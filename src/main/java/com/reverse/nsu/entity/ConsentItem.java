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
@Table(name = "CONSENT_ITEM")
public class ConsentItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer consentItemId;

    @Column(nullable = false, length = 100)
    private String consentName;

    @Column(nullable = false)
    private Boolean isRequired;

    @Column(nullable = false)
    private Integer sortOrder;

    @Column(nullable = false)
    private Boolean isActive;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime modifiedDate;
}
