package org.example.backend.domain.reservation.repository;

import org.example.backend.domain.reservation.entity.Reservation;
import org.example.backend.domain.seat.entity.Seat;
import org.example.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT r FROM Reservation r WHERE r.reservationId = :reservationId AND r.user.email = :email")
    Optional<Reservation> findByReservationIdAndUserEmail(@Param("reservationId") Long reservationId, @Param("email") String email);

    @Query("SELECT r FROM Reservation r WHERE r.user.email = :email ORDER BY r.reservationDate DESC")
    List<Reservation> findByUserEmailOrderByPaymentDateDesc(@Param("email") String email);

    Optional<Reservation> findByTicketId(String ticketId);

    Optional findByUserAndSeat(User user, Seat seat);
}
