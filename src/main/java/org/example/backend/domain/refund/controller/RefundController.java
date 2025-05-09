package org.example.backend.domain.refund.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.domain.refund.service.RefundService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/refund")
@RequiredArgsConstructor
public class RefundController {

    private final RefundService refundService;

    @PostMapping("/{paymentId}")
    public ResponseEntity<?> refund(@PathVariable Long paymentId, @RequestParam String reason) {
        try {
            refundService.refundPayment(paymentId, reason);
            return ResponseEntity.ok("환불 성공");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("❌ 환불 실패: " + e.getMessage());
        }
    }
}