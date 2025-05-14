package org.example.backend.domain.payment.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.domain.payment.dto.request.PaymentVerificationRequest;
import org.example.backend.domain.payment.repository.PaymentRepository;
import org.example.backend.domain.payment.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody PaymentVerificationRequest request) {
        try {
            paymentService.verifyAndSavePayment(request);
            return ResponseEntity.ok("결제 완료");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("❌ 결제 검증 실패: " + e.getMessage());
        }
    }

    @GetMapping("/imp/{imp_uid}")
    public ResponseEntity<?> getPaymentByImpUid(@PathVariable String impUid) {
        var payment = paymentRepository.findByImpUid(impUid)
                .orElseThrow(() -> new RuntimeException("결제 내역이 없습니다."));
        return ResponseEntity.ok(payment);
    }

}
