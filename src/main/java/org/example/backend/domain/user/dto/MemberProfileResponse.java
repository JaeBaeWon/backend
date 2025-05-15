package org.example.backend.domain.user.dto;

import lombok.Builder;
import lombok.Data;
import org.example.backend.domain.user.entity.Gender;

import java.time.LocalDate;

@Data
@Builder
public class MemberProfileResponse {
    private Gender gender;
    private String zipCode;
    private String streetAdr;
    private String detailAdr;
    private String phone;
    private LocalDate birthDate;
}
