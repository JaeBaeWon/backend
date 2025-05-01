package org.example.backend.domain.seat.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.domain.seat.dto.SeatStatusMessage;
import org.example.backend.domain.seat.service.SeatStatusPublisher;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seat")
@RequiredArgsConstructor
public class SeatController {

    private final SeatStatusPublisher publisher;

    @PostMapping("/publish")
    public String publish(@RequestBody SeatStatusMessage message) {
        publisher.publishSeatStatus(message);
        return "published";
    }
}


