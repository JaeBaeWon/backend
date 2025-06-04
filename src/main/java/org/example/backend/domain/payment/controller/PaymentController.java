package org.example.backend.domain.payment.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.domain.auth.config.CustomSecurityUserDetails;
import org.example.backend.domain.auth.service.MemberService;
import org.example.backend.domain.payment.dto.request.PaymentVerificationRequest;
import org.example.backend.domain.payment.dto.response.PaymentCompleteResponse;

import org.example.backend.domain.payment.repository.PaymentRepository;
import org.example.backend.domain.payment.service.PaymentService;
import org.example.backend.domain.reservation.dto.ReservationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final MemberService memberService;

    @PostMapping("/verify-redis")
    public ResponseEntity<?> verifyWithRedis(@RequestBody PaymentVerificationRequest request, Authentication auth) {

        if (auth == null || !(auth.getPrincipal() instanceof CustomSecurityUserDetails userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        request.setUserId(userDetails.getUserId());

        try {
            ReservationResponse response = paymentService.verifyAndQueueReservation(request);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "❌ 결제 검증 실패: " + e.getMessage()));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody PaymentVerificationRequest request, Authentication auth) {

        if (auth == null || !(auth.getPrincipal() instanceof CustomSecurityUserDetails userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = userDetails.getUserId();
        request.setUserId(userId);

        try {
            ReservationResponse response = paymentService.verifyAndCreateReservationAndSavePayment(request);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "❌ 결제 검증 실패: " + e.getMessage()));
        }
    }



    @GetMapping("/imp/{imp_uid}")
    public ResponseEntity<?> getPaymentByImpUid(@PathVariable String impUid, Authentication auth) {

        boolean check = memberService.isAuthenticated(auth);
        if (!check) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        var payment = paymentRepository.findByImpUid(impUid)
                .orElseThrow(() -> new RuntimeException("결제 내역이 없습니다."));
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/info/{reservationId}")
    public ResponseEntity<?> getPaymentCompleteInfo(@PathVariable Long reservationId, Authentication auth) {

        boolean check = memberService.isAuthenticated(auth);
        if (!check) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        PaymentCompleteResponse response = paymentService.getPaymentInfoByReservation(reservationId);
        return ResponseEntity.ok(response);
    }

}
