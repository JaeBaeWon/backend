package org.example.backend.domain.payment.dto.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentVerificationRequest {
    private String impUid;
    private String merchantUid;
    private Long userId;
    private Long performanceId;
    private Long seatId;
}

