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
                        .title("100석 연극 테스트")
                        .description("더미 데이터용 연극")
                        .category(PerformanceCategory.PLAY)
                        .performCode("PLAY-100")
                        .performStartAt(LocalDateTime.now())
                        .performEndAt(LocalDateTime.now().plusHours(2))
                        .location("서울 테스트 극장")
                        .price(50000)
                        .views(0L)
                        .totalSeats(100)
                        .remainSeats(100)
                        .performanceStatus(PerformanceStatus.UPCOMING)
                        .build()
        );

        // 2. 좌석 100개 생성 (예: A1 ~ A50, B1 ~ B50)
        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            String section = (i <= 50) ? "A" : "B";
            String seatNum = section + (i <= 50 ? i : (i - 50));
            seats.add(Seat.builder()
                    .seatNum(seatNum)
                    .seatSection(section)
                    .seatReserved(false)
                    .performance(performance)
                    .build());
        }
        seatRepository.saveAll(seats);

        System.out.println("✅ 100석 연극 공연 더미 데이터 삽입 완료");
    }
}
