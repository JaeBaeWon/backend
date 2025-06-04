package org.example.backend.domain.seat.controller;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.example.backend.domain.auth.service.MemberService;
import org.example.backend.domain.seat.dto.SeatStatusDto;
import org.example.backend.domain.seat.service.SeatService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/seat")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;
    private final MemberService memberService;

    /**
     * 공연의 모든 좌석 상태 조회 (결제 완료 or 임시 선점 포함)
     */
    @GetMapping("/status/{performId}")
    public ResponseEntity<List<SeatStatusDto>> getAllSeatsStatus(@PathVariable Long performId, Authentication auth) {

        boolean check = memberService.isAuthenticated(auth);
        if (!check) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<SeatStatusDto> seats = seatService.getAllSeatsStatus(performId);
        return ResponseEntity.ok(seats);
    }

    /**
     * 좌석 선택 시 선점 시도 (락 획득 + Redis에 선점 정보 저장)
     */
    @PostMapping("/try/{seatId}")
    public ResponseEntity<Map<String, Object>> trySelectSeat(@PathVariable Long seatId, Authentication auth) {

        boolean check = memberService.isAuthenticated(auth);
        if (!check) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("seatId", seatId, "status", "UNAUTHORIZED", "message", "로그인이 필요합니다.")
            );
        }

        boolean result = seatService.tryLockSeat(seatId);
        if (result) {
            return ResponseEntity.ok(
                    Map.of("seatId", seatId, "status", "SUCCESS", "message", "좌석 선점 성공")
            );
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Map.of("seatId", seatId, "status", "FAILED", "message", "이미 선점된 좌석입니다.")
            );
        }
    }


}
