package org.example.backend.domain.reservation.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.domain.performance.entity.Performance;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    @PostMapping("/create")
    public ResponseEntity<Reservation> createReservation(@RequestBody ReservationDto request) {
        Reservation reservation = reservationService.createReservation(request);
        return ResponseEntity.ok(reservation);
    }

    @GetMapping("/check/user/{userId}")
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
    }

    @Transactional(readOnly = true)
    @GetMapping("/check/reservation/{reservationId}")
    public ResponseEntity<ReservationResponse> getCheckoutInfo(@PathVariable Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("예약을 찾을 수 없습니다."));
        User user = reservation.getUserId();
        Performance performance = reservation.getPerformanceId();

        System.out.println("user: " + user.getUsername() + ", price: " + performance.getPrice());

        ReservationResponse response = ReservationResponse.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .phone(user.getPhone())
                .title(performance.getTitle())
                .price(performance.getPrice())
                .build();

        return ResponseEntity.ok(response);
    }

}
