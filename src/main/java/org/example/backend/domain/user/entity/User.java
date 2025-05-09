package org.example.backend.domain.user.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "Users")
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String email;

    private String password;

    @Column(name = "user_name")
    private String username;

    private Date birthday;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String phone;

    private String address;

    @Enumerated(EnumType.STRING)
    private LoginType loginType;
}
