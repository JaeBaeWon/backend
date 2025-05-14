package org.example.backend.domain.payment.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.domain.payment.dto.PaymentDto;
import org.example.backend.domain.payment.dto.request.PaymentVerificationRequest;
import org.example.backend.domain.payment.dto.response.PaymentCompleteResponse;
import org.example.backend.domain.payment.entity.Payment;
import org.example.backend.domain.payment.repository.PaymentRepository;
import org.example.backend.domain.payment.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody PaymentVerificationRequest request) {
        try {
            var reservation = paymentService.verifyAndCreateReservationAndSavePayment(request);

            // ✅ JSON으로 응답
            return ResponseEntity.ok().body(
                    java.util.Map.of(
                            "message", "결제 및 예매 완료",
                            "reservationId", reservation.getReservationId()
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "❌ 결제 검증 실패: " + e.getMessage()));
        }
    }

    @GetMapping("/imp/{imp_uid}")
    public ResponseEntity<?> getPaymentByImpUid(@PathVariable String impUid) {
        var payment = paymentRepository.findByImpUid(impUid)
                .orElseThrow(() -> new RuntimeException("결제 내역이 없습니다."));
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/info/{reservationId}")
    public ResponseEntity<?> getPaymentCompleteInfo(@PathVariable Long reservationId) {
        PaymentCompleteResponse response = paymentService.getPaymentInfoByReservation(reservationId);
        return ResponseEntity.ok(response);
    }

}
