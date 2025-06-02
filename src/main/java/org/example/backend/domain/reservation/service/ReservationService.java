package org.example.backend.domain.reservation.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.domain.performance.entity.Performance;
import org.example.backend.domain.performance.repository.PerformanceRepository;
import org.example.backend.domain.reservation.dto.ReservationDto;
import org.example.backend.domain.reservation.entity.Reservation;
import org.example.backend.domain.reservation.entity.ReservationStatus;
import org.example.backend.domain.reservation.repository.ReservationRepository;
import org.example.backend.domain.seat.entity.Seat;
import org.example.backend.domain.seat.entity.SeatStatus;
import org.example.backend.domain.seat.repository.SeatRepository;
import org.example.backend.domain.user.entity.User;
import org.example.backend.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;


import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationService {

    @PersistenceContext
    private EntityManager em;

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final PerformanceRepository performanceRepository;
    private final SeatRepository seatRepository;

    @Transactional
    public Reservation createReservation(ReservationDto request) {
        em.flush();  // DB 반영
        em.clear();  // 영속성 컨텍스트 비우기

        System.out.println("👉 예약 요청 도착: userId=" + request.getUserId() +
                ", performanceId=" + request.getPerformanceId() +
                ", seatId=" + request.getSeatId());

        // 사용자, 공연, 좌석 조회
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Performance performance = performanceRepository.findById(request.getPerformanceId())
                .orElseThrow(() -> new IllegalArgumentException("공연을 찾을 수 없습니다."));
        Seat seat = seatRepository.findById(request.getSeatId())
                .orElseThrow(() -> new IllegalArgumentException("좌석을 찾을 수 없습니다."));

        // 좌석이 이미 HOLD or BOOKED 상태인지 확인
        System.out.println("⛳ 현재 좌석 상태: " + seat.getSeatStatus());

        if (seat.getSeatStatus() != SeatStatus.AVAILABLE) {
            throw new IllegalStateException("이미 예약 중이거나 확정된 좌석입니다.");
        }

        // 좌석 상태 → HOLD
        seat.setSeatStatus(SeatStatus.HOLD);
        seatRepository.save(seat);

        // 예약 생성
        Reservation reservation = Reservation.builder()
                .userId(user)
                .performanceId(performance)
                .seatId(seat)
                .reservationStatus(ReservationStatus.PENDING)  // 결제 전 상태
                .ticketId(UUID.randomUUID().toString().replace("-", "").substring(0, 12))        // 티켓 고유 ID
                .build();

        return reservationRepository.save(reservation);
    }
    
}
