package org.example.backend.domain.seat.service;

import static org.example.backend.global.exception.ExceptionContent.ALREADY_RESERVED;
import static org.example.backend.global.exception.ExceptionContent.NOT_FOUND_PERFORMANCE;
import static org.example.backend.global.exception.ExceptionContent.NOT_FOUND_SEAT;

import jakarta.transaction.Transactional;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.domain.performance.entity.Performance;
import org.example.backend.domain.performance.repository.PerformanceRepository;
import org.example.backend.domain.seat.dto.SeatStatusDto;
import org.example.backend.domain.seat.entity.Seat;
import org.example.backend.domain.seat.entity.SeatStatus;
import org.example.backend.domain.seat.repository.SeatRepository;
import org.example.backend.global.exception.CustomException;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class SeatService {

    private final SeatRepository seatRepository;
    private final PerformanceRepository performanceRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final RedissonClient redissonClient;

    private static final String SEAT_LOCK_PREFIX = "lock:seat:";
    private static final String SEAT_STATUS_PREFIX = "seat:";

    /**
     * 좌석 선점 시도 (Redis에만 저장, DB 반영 X)
     */
    public boolean tryLockSeat(Long seatId) {
        RLock lock = redissonClient.getLock(SEAT_LOCK_PREFIX + seatId);

        try {
            if (lock.tryLock(1, 5, TimeUnit.SECONDS)) {
                Seat seat = seatRepository.findById(seatId)
                        .orElseThrow(() -> new CustomException(NOT_FOUND_SEAT));

                if (!seat.getSeatStatus().equals(SeatStatus.AVAILABLE)) {
                    log.info("❌ 이미 선점된 좌석입니다: {}", seatId);
                    return false;
                }

                String redisKey = SEAT_STATUS_PREFIX + seatId;
                if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
                    log.info("❌ 이미 선점된 좌석입니다 (Redis 기준): {}", seatId);
                    return false;
                }

                redisTemplate.opsForValue().set(redisKey, "RESERVED", Duration.ofMinutes(3));
                log.info("✅ 좌석 선점 성공: {}", seatId);
                return true;
            }
            else {
                log.warn("❌ 락 획득 실패: {}", seatId);
                return false;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 공연의 전체 좌석 상태 조회
     */
    public List<SeatStatusDto> getAllSeatsStatus(Long performId) {
        Performance perform = performanceRepository.findById(performId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_PERFORMANCE));

        List<Seat> seats = seatRepository.findAllByPerformance(perform);

        return seats.stream()
                .map(seat -> {
                    boolean isLocked = Boolean.TRUE.equals(
                            redisTemplate.hasKey(SEAT_STATUS_PREFIX + seat.getSeatId())
                    );

                    boolean isReserved = seat.getSeatStatus().equals(SeatStatus.AVAILABLE) || isLocked;

                    return SeatStatusDto.of(seat, isReserved);
                })
                .toList();
    }
}
