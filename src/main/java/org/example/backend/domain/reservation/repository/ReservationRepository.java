package org.example.backend.domain.reservation.repository;

import org.example.backend.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByReservationIdAndUserEmail(Long reservationId, String email);

    List<Reservation> findByUserEmailOrderByPaymentDateDesc(String email);

    Optional<Reservation> findByTicketId(String ticketId);
}
