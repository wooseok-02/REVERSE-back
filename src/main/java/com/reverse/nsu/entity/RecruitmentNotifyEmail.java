package com.reverse.nsu.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Getter @Setter @NoArgsConstructor
@Table(name = "RECRUITMENT_NOTIFY_EMAIL")
public class RecruitmentNotifyEmail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer notifyId;

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @NotBlank
    private String email;

    private Boolean isActive = true;

    @Column(columnDefinition = "CHAR(36)")
    private String unsubscribeKey = UUID.randomUUID().toString();
}