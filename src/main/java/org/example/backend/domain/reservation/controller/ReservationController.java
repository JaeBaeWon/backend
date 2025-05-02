package org.example.backend.domain.reservation.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.domain.user.dto.UserDto;
import org.example.backend.domain.user.entity.User;
import org.example.backend.domain.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final UserRepository userRepository;

    @GetMapping("/check/{userId}")
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
}
