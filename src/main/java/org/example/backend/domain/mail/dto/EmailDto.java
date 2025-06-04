// src/main/java/org/example/backend/domain/mail/dto/EmailDto.java
package org.example.backend.domain.mail.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime paymentDate;
}
