package org.example.backend.domain.auth.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class FindIdRequest {
    private String phone;
    private LocalDate birthday;
}
