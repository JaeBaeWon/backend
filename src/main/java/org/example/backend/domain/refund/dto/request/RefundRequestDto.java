package org.example.backend.domain.refund.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefundRequestDto {
    private Long paymentId;
    private String refundReason;
}