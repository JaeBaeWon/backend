/*
package org.example.backend.domain.seat.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.backend.domain.performance.entity.Performance;
import org.example.backend.domain.performance.entity.PerformanceCategory;
import org.example.backend.domain.performance.entity.PerformanceStatus;
import org.example.backend.domain.performance.repository.PerformanceRepository;
import org.example.backend.domain.seat.entity.Seat;
import org.example.backend.domain.seat.entity.SeatStatus;
import org.example.backend.domain.seat.repository.SeatRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeatDummyDataInitializer implements CommandLineRunner {

    private final PerformanceRepository performanceRepository;
    private final SeatRepository seatRepository;

    @Override
    public void run(String... args) {
        // 1. 공연 생성
        Performance performance = performanceRepository.save(
                Performance.builder()
                        .title("연극 테스트")
                        .description("더미 데이터용 연극")
                        .category(PerformanceCategory.PLAY)
                        .performanceCode("PLAY-100")
                        .performanceStartAt(LocalDateTime.now())
                        .performanceEndAt(LocalDateTime.now().plusHours(2))
                        .location("서울 테스트 극장")
                        .price(500)
                        .views(0L)
                        .totalSeats(600)
                        .remainSeats(600)
                        .performanceStatus(PerformanceStatus.UPCOMING)
                        .build()
        );

        // 2. 좌석 600개 생성
        List<Seat> seats = new ArrayList<>();

        for (char section = 'A'; section <= 'F'; section++) {
            for (int number = 1; number <= 100; number++) {
                Seat seat = Seat.builder()
                        .seatSection(String.valueOf(section))  // A ~ F
                        .seatNum(String.valueOf(number))      // 1 ~ 100
                        .seatStatus(SeatStatus.AVAILABLE)     // 기본 상태
                        .performance(performance)
                        .build();
                seats.add(seat);
            }
        }
        seatRepository.saveAll(seats);

        System.out.println("✅ 600석 연극 공연 더미 데이터 삽입 완료");
    }
}
*/
