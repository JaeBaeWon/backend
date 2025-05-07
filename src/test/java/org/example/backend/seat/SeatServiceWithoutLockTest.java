package org.example.backend.seat;

import lombok.extern.slf4j.Slf4j;
import org.example.backend.domain.performance.entity.*;
import org.example.backend.domain.performance.repository.PerformanceRepository;
import org.example.backend.domain.seat.entity.Seat;
import org.example.backend.domain.seat.repository.SeatRepository;
import org.example.backend.domain.seat.service.SeatService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class SeatServiceWithoutLockTest {

    @Autowired
    SeatService seatService;

    @Autowired
    SeatRepository seatRepository;

    @Autowired
    PerformanceRepository performanceRepository;

    private Long seatId;
    private final int CONCURRENT_COUNT = 10;

    @BeforeEach
    void setup() {
        Performance performance = performanceRepository.save(
                Performance.builder()
                        .title("락 없음 테스트")
                        .description("동시성 충돌")
                        .category(PerformanceCategory.CONCERT)
                        .performCode("NOLOCK-01")
                        .performStartAt(LocalDateTime.now())
                        .performEndAt(LocalDateTime.now().plusHours(2))
                        .location("서울")
                        .price(10000)
                        .views(0L)
                        .totalSeats(1)
                        .remainSeats(1)
                        .performanceStatus(PerformanceStatus.UPCOMING)
                        .build()
        );

        Seat seat = seatRepository.saveAndFlush(
                Seat.builder()
                        .seatNum("A-NOLOCK")
                        .seatSection("A")
                        .seatReserved(false)
                        .performance(performance)
                        .build()
        );

        seatId = seat.getSeatId();
    }

    @AfterEach
    void tearDown() {
        seatRepository.deleteAll();
        performanceRepository.deleteAll();
    }

    @Test
    @DisplayName("Redisson 락 미적용: 여러 명이 선점 가능")
    void multipleSuccessWithoutLock() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(CONCURRENT_COUNT);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < CONCURRENT_COUNT; i++) {
            final int threadNum = i; // 로그에 몇 번째 스레드인지 표시용

            executor.submit(() -> {
                try {
                    log.info("[Thread-{}] 좌석 선점 시도 시작", threadNum);

                    if (seatService.tryReserveSeatWithoutLock(seatId)) {
                        int current = successCount.incrementAndGet();
                        log.info("✅ [Thread-{}] 선점 성공! 현재 성공 횟수 = {}", threadNum, current);
                    } else {
                        log.info("❌ [Thread-{}] 선점 실패", threadNum);
                    }

                } catch (Exception e) {
                    log.error("[Thread-{}] 예외 발생: {}", threadNum, e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        log.info("🎯 최종 성공한 사용자 수: {}", successCount.get());

        assertTrue(successCount.get() > 1);
    }

}
