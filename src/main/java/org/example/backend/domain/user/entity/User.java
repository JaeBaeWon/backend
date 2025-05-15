package org.example.backend.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "user_name", nullable = false)
    private String username;

    @Column(nullable = false)
    private boolean onboardingCompleted = false;

    private String provider;
    private String providerId;

    private LocalDate birthday;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String zipCode;     // 우편번호
    private String streetAdr;   // 도로명 주소
    private String detailAdr;   // 상세 주소

    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    public void updateOnboardingInfo(Gender gender, String zipCode, String streetAdr, String detailAdr, String phone, LocalDate birthday) {
        this.gender = gender;
        this.zipCode = zipCode;
        this.streetAdr = streetAdr;
        this.detailAdr = detailAdr;
        this.phone = phone;
        this.birthday = birthday;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // User.java
    public String getAddress() {
        return String.format("%s %s (%s)",
                this.getStreetAdr(),
                this.getDetailAdr(),
                this.getZipCode());
    }

}
