package com.reverse.nsu.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "USER_TOKEN")
@Getter
@NoArgsConstructor
public class UserToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tokenId")
    private Long tokenId;

    @Column(name = "userId", nullable = false, length = 15)
    private String userId;

    @Column(name = "accessToken", nullable = false, length = 512)
    private String accessToken;

    @Column(name = "refreshToken", nullable = false, length = 512)
    private String refreshToken;

    @Column(name = "accessTokenExpiry", nullable = false)
    private LocalDateTime accessTokenExpiry;

    @Column(name = "refreshTokenExpiry", nullable = false)
    private LocalDateTime refreshTokenExpiry;

    @Column(name = "deviceInfo", length = 255)
    private String deviceInfo;

    @Column(name = "ipAddress", length = 45)
    private String ipAddress;

    @Column(name = "isRevoked", nullable = false)
    private Boolean isRevoked = false;

    @Column(name = "createdDate", nullable = false, updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @UpdateTimestamp
    @Column(name = "modifiedDate", nullable = false)
    private LocalDateTime modifiedDate;

    public static UserToken create(String userId, String accessToken, String refreshToken,
                                   LocalDateTime accessTokenExpiry, LocalDateTime refreshTokenExpiry,
                                   String deviceInfo, String ipAddress) {
        UserToken token = new UserToken();
        token.userId = userId;
        token.accessToken = accessToken;
        token.refreshToken = refreshToken;
        token.accessTokenExpiry = accessTokenExpiry;
        token.refreshTokenExpiry = refreshTokenExpiry;
        // User-Agent는 255자 초과 가능하므로 컬럼 길이에 맞게 자름
        token.deviceInfo = (deviceInfo != null && deviceInfo.length() > 255)
                ? deviceInfo.substring(0, 255) : deviceInfo;
        token.ipAddress = ipAddress;
        return token;
    }

    public void revoke() {
        this.isRevoked = true;
    }

    public void updateAccessToken(String accessToken, LocalDateTime accessTokenExpiry) {
        this.accessToken = accessToken;
        this.accessTokenExpiry = accessTokenExpiry;
    }
}
