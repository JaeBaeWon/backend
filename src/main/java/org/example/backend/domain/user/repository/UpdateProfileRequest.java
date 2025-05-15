package org.example.backend.domain.user.repository;

import lombok.Data;
import org.example.backend.domain.user.entity.Gender;

import java.time.LocalDate;

@Data
public class UpdateProfileRequest {
    private Gender gender;
    private String zipCode;
    private String streetAdr;
    private String detailAdr;
    private String phone;
    private LocalDate birthDate;
}