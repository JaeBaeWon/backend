package org.example.backend.domain.payment.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentCompleteResponse {
    private String ticketNumber;
    private String performanceTitle;
    private String performanceLocation;
    private String performanceDate;
    private String seatInfo;
    private int paymentAmount;
    private String payType;
    private String paymentTime;
}
