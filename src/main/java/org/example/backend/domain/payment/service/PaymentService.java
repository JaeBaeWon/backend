package org.example.backend.domain.payment.service;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.response.IamportResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.backend.domain.payment.dto.request.PaymentVerificationRequest;
import org.example.backend.domain.payment.entity.PayType;
import org.example.backend.domain.payment.entity.Payment;
import org.example.backend.domain.payment.entity.PaymentStatus;
import org.example.backend.domain.payment.repository.PaymentRepository;
import org.example.backend.domain.reservation.entity.Reservation;
import org.example.backend.domain.reservation.entity.ReservationStatus;
import org.example.backend.domain.reservation.repository.ReservationRepository;
import org.example.backend.domain.seat.entity.Seat;
import org.example.backend.domain.seat.entity.SeatStatus;
import org.example.backend.domain.seat.repository.SeatRepository;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final IamportClient iamportClient;
    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;

    @Transactional
    public void verifyAndSavePayment(PaymentVerificationRequest request) throws Exception {

        System.out.println("🔍 imp_uid: " + request.getImpUid());
        System.out.println("🔍 merchant_uid: " + request.getMerchantUid());
        System.out.println("🔍 reservationId: " + request.getReservationId());

        // 1. 아임포트 서버에서 결제 정보 검증
        IamportResponse<com.siot.IamportRestClient.response.Payment> iamportResponse =
                iamportClient.paymentByImpUid(request.getImpUid());

        if (iamportResponse.getResponse() == null) {
            throw new IllegalStateException("아임포트에서 결제 정보를 찾을 수 없습니다.");
        }

        com.siot.IamportRestClient.response.Payment paymentInfo = iamportResponse.getResponse();

        System.out.println("✅ 아임포트 응답 받은 결제 정보");
        System.out.println("  - status: " + paymentInfo.getStatus());
        System.out.println("  - amount: " + paymentInfo.getAmount());
        System.out.println("  - payMethod: " + paymentInfo.getPayMethod());

        if (!paymentInfo.getStatus().equals("paid")) {
            throw new IllegalStateException("결제가 완료되지 않았습니다.");
        }

        // 2. 예약 정보 조회
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("해당 예약 정보가 없습니다."));

        System.out.println("✅ 예약된 공연 금액: " + reservation.getPerformanceId().getPrice());

        // 3. 금액 일치 여부 확인
        if (paymentInfo.getAmount().intValue() != reservation.getPerformanceId().getPrice()) {
            throw new IllegalStateException("결제 금액이 일치하지 않습니다.");
        }

        // 4. 결제 정보 저장
        Payment payment = Payment.builder()
                .impUid(request.getImpUid())
                .merchantUid(request.getMerchantUid())
                .paymentAmount(paymentInfo.getAmount().intValue())
                .paymentStatus(PaymentStatus.SUCCESS)
                .payType(PayType.valueOf(paymentInfo.getPayMethod().toUpperCase()))
                .paymentDate(new Date())
                .reservation(reservation)
                .build();

        paymentRepository.save(payment);

        // 5. Reservation 상태 업데이트
        reservation.updateStatus(ReservationStatus.RESERVED);

        // 6. Seat 상태 업데이트
        Seat seat = reservation.getSeatId();
        seat.updateStatus(SeatStatus.BOOKED);

        // 7. 저장
        seatRepository.save(seat);
        reservationRepository.save(reservation);
    }
}
