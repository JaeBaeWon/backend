package org.example.backend.domain.seat.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.domain.seat.dto.SeatStatusMessage;
import org.example.backend.domain.seat.service.SeatService;
import org.example.backend.domain.seat.service.SeatStatusPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seat")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;
    private final SeatStatusPublisher publisher;

    @PostMapping("/{seatId}/lock")
    public ResponseEntity<String> reserveWithLock(@PathVariable Long seatId) {
        boolean success = seatService.tryLockSeat(seatId);
        return success ? ResponseEntity.ok("락 선점 성공") :
                ResponseEntity.status(409).body("이미 선점됨");
    }

    @PostMapping("/publish")
    public String publish(@RequestBody SeatStatusMessage message) {
        publisher.publishSeatStatus(message);
        return "published";
    }
}


