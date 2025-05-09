package org.example.backend.domain.payment.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.domain.payment.dto.PaymentDto;
import org.example.backend.domain.payment.entity.Payment;
import org.example.backend.domain.payment.service.PaymentService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public Payment createPayment(@RequestBody PaymentDto dto) {
        return paymentService.createPayment(dto);
    }
}
