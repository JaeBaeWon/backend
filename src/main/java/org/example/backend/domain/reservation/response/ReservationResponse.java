package org.example.backend.domain.reservation.response;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReservationResponse {
    private String email;
    private String username;
    private String phone;

    private String title;  // 공연명
    private int price;    // 결제 금액
}
