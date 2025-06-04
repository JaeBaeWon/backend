// src/main/java/org/example/backend/domain/mail/dto/EmailDto.java
package org.example.backend.domain.mail.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

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
    private LocalDateTime paymentDate;
}
