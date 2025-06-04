// src/main/java/org/example/backend/domain/mail/dto/EmailDto.java
package org.example.backend.domain.mail.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class EmailDto {
    private String email;
    private String username;
    private String title;
    private String performStartAt;
    private String performEndAt;
    private String location;
    private String seatSection;
    private String seatNum;
    private int paymentAmount;
    private Date paymentDate;
}
