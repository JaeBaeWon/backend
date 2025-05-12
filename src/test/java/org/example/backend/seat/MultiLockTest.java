package org.example.backend.seat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.domain.performance.entity.Performance;
import org.example.backend.domain.performance.entity.PerformanceCategory;
import org.example.backend.domain.performance.entity.PerformanceStatus;
import org.example.backend.domain.performance.repository.PerformanceRepository;
import org.example.backend.domain.seat.entity.Seat;
import org.example.backend.domain.seat.entity.SeatStatus;
import org.example.backend.domain.seat.repository.SeatRepository;
import org.example.backend.domain.seat.service.SeatService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;


@Slf4j
@SpringBootTest
public class MultiLockTest {
    @Autowired
    SeatService seatService;

    @Autowired
    SeatRepository seatRepository;

    @Autowired
    PerformanceRepository performanceRepository;

    @Autowired
    StringRedisTemplate redisTemplate;

    private final int SEAT_COUNT = 10;
    private final int CONCURRENT_PER_SEAT = 100;
    private List<Long> seatIds;

    @BeforeEach
    void setup() {
        Performance performance = performanceRepository.save(
                Performance.builder()
                        .title("멀티 좌석 락 테스트")
                        .description("동시성 분산락 테스트")
                        .category(PerformanceCategory.PLAY)
                        .performanceCode("LOCK-MULTI")
                        .performanceStartAt(LocalDateTime.now())
                        .performanceEndAt(LocalDateTime.now().plusHours(2))
                        .location("부산")
                        .price(15000)
                        .views(0L)
                        .totalSeats(100)
                        .remainSeats(100)
                        .performanceStatus(PerformanceStatus.UPCOMING)
                        .build()
        );

        seatIds = new ArrayList<>();

        for (int i = 0; i < SEAT_COUNT; i++) {
            Seat seat = seatRepository.saveAndFlush(
                    Seat.builder()
                            .seatNum("S-" + i)
                            .seatSection("A")
                            .seatStatus(SeatStatus.HOLD)
                            .performance(performance)
                            .build()
            );
            seatIds.add(seat.getSeatId());
        }
    }

    @AfterEach
    void cleanup() {
        seatRepository.deleteAll();
        performanceRepository.deleteAll();
        for (Long seatId : seatIds) {
            redisTemplate.delete("seat:" + seatId);
        }
    }

    @Test
    @DisplayName("10개의 좌석에 대해 각각 100명이 동시에 선점 시도 → 좌석당 1명만 성공")
    void multiSeatConcurrencyTest() throws InterruptedException {
        Map<Long, AtomicInteger> successMap = new ConcurrentHashMap<>();

        for (Long seatId : seatIds) {
            successMap.put(seatId, new AtomicInteger(0));
            ExecutorService executor = Executors.newFixedThreadPool(10);
            CountDownLatch latch = new CountDownLatch(CONCURRENT_PER_SEAT);

            for (int i = 0; i < CONCURRENT_PER_SEAT; i++) {
                executor.submit(() -> {
                    try {
                        if (seatService.tryLockSeat(seatId)) {
                            successMap.get(seatId).incrementAndGet();
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();
            executor.shutdown();
        }

        // 결과 로그 및 검증
        for (Long seatId : seatIds) {
            int success = successMap.get(seatId).get();
            log.info("🎯 Seat ID {} → 성공 수: {}", seatId, success);
            assertEquals(1, success, "좌석 " + seatId + "에는 1명만 성공해야 합니다.");
        }
    }

}
