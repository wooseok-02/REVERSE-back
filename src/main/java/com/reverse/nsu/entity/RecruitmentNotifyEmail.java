package com.reverse.nsu.entity;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "RECRUITMENT_NOTIFY_EMAIL", uniqueConstraints = {
        @UniqueConstraint(name = "UQ_NOTIFY_EMAIL", columnNames = {"email"}),
        @UniqueConstraint(name = "UQ_NOTIFY_UNSUBSCRIBE_KEY", columnNames = {"unsubscribeKey"})
})
public class RecruitmentNotifyEmail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notifyId")
    private Integer notifyId;

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @NotBlank
    @Column(name = "email", length = 255, nullable = false)
    private String email;

    @Column(name = "isActive", columnDefinition = "TINYINT(1)", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "unsubscribeKey", columnDefinition = "CHAR(36)", nullable = false)
    @Builder.Default
    private String unsubscribeKey = UUID.randomUUID().toString();

    @Column(name = "createdDate", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "modifiedDate", nullable = false)
    private LocalDateTime modifiedDate;

    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
        this.modifiedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.modifiedDate = LocalDateTime.now();
    }
}