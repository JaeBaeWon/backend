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
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDateTime;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class SeatServiceWithLockTest {

    @Autowired
    SeatService seatService;

    @Autowired
    SeatRepository seatRepository;

    @Autowired
    PerformanceRepository performanceRepository;

    @Autowired
    StringRedisTemplate redisTemplate;

    private Long seatId;
    private final int CONCURRENT_COUNT = 100;
    private String redisSeatKey;

    @BeforeEach
    void setup() {
        Performance performance = performanceRepository.save(
                Performance.builder()
                        .title("락 테스트")
                        .description("분산락")
                        .category(PerformanceCategory.CONCERT)
                        .performCode("LOCK-01")
                        .performStartAt(LocalDateTime.now())
                        .performEndAt(LocalDateTime.now().plusHours(2))
                        .location("서울")
                        .price(10000)
                        .views(100L)
                        .totalSeats(100)
                        .remainSeats(100)
                        .performanceStatus(PerformanceStatus.UPCOMING)
                        .build()
        );

        Seat seat = seatRepository.saveAndFlush(
                Seat.builder()
                        .seatNum("A-LOCK")
                        .seatSection("A")
                        .seatReserved(false)
                        .performance(performance)
                        .build()
        );

        seatId = seat.getSeatId();
        redisSeatKey = "seat:" + seatId;
    }

    @AfterEach
    void tearDown() {
        seatRepository.deleteAll();
        performanceRepository.deleteAll();
        redisTemplate.delete(redisSeatKey); // Redis 상태 초기화
    }

    @Test
    @DisplayName("Redisson 락 적용 + Redis 선점 로직: 1명만 성공")
    void onlyOneSuccessWithLock() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        CountDownLatch latch = new CountDownLatch(CONCURRENT_COUNT);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < CONCURRENT_COUNT; i++) {
            final int threadNum = i;
            executor.submit(() -> {
                try {
                    log.info("[Thread-{}] 좌석 선점 시도 시작", threadNum);
                    if (seatService.tryLockSeat(seatId)) {
                        int current = successCount.incrementAndGet();
                        log.info("✅ [Thread-{}] 선점 성공! 현재 성공 수: {}", threadNum, current);
                    } else {
                        log.info("❌ [Thread-{}] 선점 실패 (락 획득 못함 or 이미 선점됨)", threadNum);
                    }
                } catch (Exception e) {
                    log.error("💥 [Thread-{}] 예외 발생: {}", threadNum, e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        log.info("🎯 최종 성공한 사용자 수: {}", successCount.get());
        assertEquals(1, successCount.get());
    }
}
