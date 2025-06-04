package org.example.backend.domain.reservation.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.domain.auth.config.CustomSecurityUserDetails;
import org.example.backend.domain.auth.service.MemberService;
import org.example.backend.domain.performance.entity.Performance;
import org.example.backend.domain.reservation.dto.ReservationDetailsDto;
import org.example.backend.domain.reservation.dto.ReservationDto;
import org.example.backend.domain.reservation.entity.Reservation;
import org.example.backend.domain.reservation.repository.ReservationRepository;
import org.example.backend.domain.reservation.response.ReservationResponse;
import org.example.backend.domain.reservation.service.ReservationService;
import org.example.backend.domain.user.dto.UserDto;
import org.example.backend.domain.user.entity.User;
import org.example.backend.domain.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final MemberService memberService;
    private final ReservationService reservationService;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    /**
     * 예약 상태 조회 (PENDING, CONFIRMED, NONE)
     */
    @GetMapping("/status/{seatId}")
    public ResponseEntity<?> getReservationStatus(@PathVariable Long seatId, Authentication auth) {
        if (auth == null || !(auth.getPrincipal() instanceof CustomSecurityUserDetails userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = userDetails.getUserId();

        try {
            String status = reservationService.getReservationStatus(userId, seatId);
            return ResponseEntity.ok(Map.of("status", status));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "❌ 예약 상태 조회 실패: " + e.getMessage()));
        }
    }

    /*@PostMapping("/create")
    public ResponseEntity<Reservation> createReservation(@RequestBody ReservationDto request) {
        Reservation reservation = reservationService.createReservation(request);
        return ResponseEntity.ok(reservation);
    }*/

    /*@GetMapping("/check/user/{userId}")
    public ResponseEntity<UserDto> getUserInfo(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자 없음"));

        UserDto dto = new UserDto(
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                user.getAddress()
        );

        return ResponseEntity.ok(dto);
    }*/

    /*@Transactional(readOnly = true)
    @GetMapping("/check/reservation/{reservationId}")
    public ResponseEntity<ReservationResponse> getCheckoutInfo(@PathVariable Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("예약을 찾을 수 없습니다."));
        User user = reservation.getUser();
        Performance performance = reservation.getPerformance();

        System.out.println("user: " + user.getUsername() + ", price: " + performance.getPrice());

        ReservationResponse response = ReservationResponse.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .phone(user.getPhone())
                .title(performance.getTitle())
                .price(performance.getPrice())
                .build();

        return ResponseEntity.ok(response);
    }*/

    @GetMapping("/by-ticket/{ticketId}")
    public ResponseEntity<?> getReservationIdByTicket(@PathVariable String ticketId) {
        return reservationRepository.findByTicketId(ticketId)
                .map(reservation -> ResponseEntity.ok(Map.of("reservationId", reservation.getReservationId())))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/reservation/{id}")
    public ResponseEntity<ReservationDetailsDto> reservationDetailPage(@PathVariable("id") Long reservationId,
                                                                       Authentication auth) {
        boolean check = memberService.isAuthenticated(auth);
        if (!check) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        ReservationDetailsDto dto = memberService.getReservationByIdAndLoginId(reservationId, auth.getName());
        return ResponseEntity.ok(dto);
    }

}
