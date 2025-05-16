package org.example.backend.domain.refund.service;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import org.example.backend.domain.payment.entity.PaymentStatus;
import org.example.backend.domain.payment.repository.PaymentRepository;
import org.example.backend.domain.refund.entity.Refund;
import org.example.backend.domain.refund.entity.RefundStatus;
import org.example.backend.domain.refund.repository.RefundRepository;
import org.example.backend.domain.reservation.entity.ReservationStatus;
import org.example.backend.domain.seat.entity.SeatStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefundService {

    private final IamportClient iamportClient;
    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;

    @Transactional
    public void refundPayment(Long paymentId, String reason) throws Exception {
        var payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 결제입니다."));

        // 1. Iamport 환불 요청
        CancelData cancelData = new CancelData(payment.getImpUid(), true);
        cancelData.setReason(reason);

        IamportResponse<Payment> cancelResponse = iamportClient.cancelPaymentByImpUid(cancelData);

        System.out.println(">>> 아임포트 환불 응답: " + cancelResponse.getMessage());
        System.out.println(">>> 응답 객체: " + cancelResponse.getResponse());
        System.out.println(">>> 상태: " + (cancelResponse.getResponse() != null ? cancelResponse.getResponse().getStatus() : "null"));

        if (cancelResponse.getResponse() == null || !cancelResponse.getResponse().getStatus().equals("cancelled")) {
            throw new IllegalStateException("아임포트 환불 실패");
        }


        if (cancelResponse.getResponse() == null || !cancelResponse.getResponse().getStatus().equals("cancelled")) {
            throw new IllegalStateException("아임포트 환불 실패");
        }

        // 2. 환불 정보 저장
        Refund refund = Refund.builder()
                .payment(payment)
                .refundAmount(cancelResponse.getResponse().getCancelAmount().intValue())
                .refundReason(reason)
                .refundStatus(RefundStatus.COMPLETED)
                .build();

        refundRepository.save(refund);

        // 3. 결제 상태 변경
        payment.updateStatus(PaymentStatus.CANCELED);

        var reservation = payment.getReservation();
        reservation.updateReservationStatus(ReservationStatus.CANCELED);

        var seat = reservation.getSeat();
        seat.updateStatus(SeatStatus.AVAILABLE);
    }
}