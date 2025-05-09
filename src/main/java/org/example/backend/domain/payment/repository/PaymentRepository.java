package org.example.backend.domain.payment.repository;

import org.example.backend.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByImpUid(String impUid);

}


