package org.example.backend.domain.payment.repository;

import org.example.backend.domain.payment.entity.Payment;
import org.example.backend.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByImpUid(String impUid);

    Optional<Payment> findByReservation(Reservation reservation);
}


