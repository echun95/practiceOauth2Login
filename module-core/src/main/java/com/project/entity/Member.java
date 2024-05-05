package com.project.entity;

import com.project.enums.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TB_MEMBER")
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long memberId;

    @Column(name = "MEMBER_NAME")
    private String name;

    @Column(name = "MEMBER_PASSWORD")
    private String password;

    @Column(name = "MEMBER_EMAIL")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "MEMBER_ROLE")
    private Role role;

    @Column(name = "PROVIDER")
    private String provider;
    @Column(name = "PROVIDER_ID")
    private String providerId;
    @Column(name = "REFRESH_TOKEN")
    private String refreshToken;

    @Builder
    public Member(String name, String password, String email, Role role, String provider, String providerId, String refreshToken) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
        this.refreshToken = refreshToken;
    }
    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
