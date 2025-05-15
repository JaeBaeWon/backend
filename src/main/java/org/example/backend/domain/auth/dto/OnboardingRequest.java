package org.example.backend.domain.auth.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.backend.domain.user.entity.Gender;

import java.time.LocalDate;

@Getter
@Setter
public class OnboardingRequest {

    private Gender gender;
    private String zipCode;
    private String streetAdr;
    private String detailAdr;
    private String phone;
    private LocalDate birthDate;
}
