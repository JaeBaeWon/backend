package org.example.backend.domain.seat.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.backend.domain.seat.entity.Seat;
import org.example.backend.domain.seat.repository.SeatRepository;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class SeatService {

    private final SeatRepository seatRepository;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate redisTemplate;

    private static final String SEAT_LOCK_PREFIX = "lock:seat:";
    private static final String SEAT_STATE_PREFIX = "seat:";

    //Redis Lock으로 좌석 이선좌 관리
    public boolean tryLockSeat(Long seatId) {
        RLock lock = redissonClient.getLock(SEAT_LOCK_PREFIX + seatId);
        String seatKey = SEAT_STATE_PREFIX + seatId;

        try {
            if (lock.tryLock(1, 10, TimeUnit.SECONDS)) {
                String seatStatus = redisTemplate.opsForValue().get(seatKey);

                if (!"RESERVED".equals(seatStatus)) {
                    // Redis에 임시 선점 상태 저장 (TTL: 5분)
                    redisTemplate.opsForValue().set(seatKey, "RESERVED", 5, TimeUnit.MINUTES);
                    return true;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

        return false;
    }

    //Redis Lock 없이 좌석 선택
    public boolean tryReserveSeatWithoutLock(Long seatId) {
        String seatKey = SEAT_STATE_PREFIX + seatId;
        String seatStatus = redisTemplate.opsForValue().get(seatKey);

        if (!"RESERVED".equals(seatStatus)) {
            redisTemplate.opsForValue().set(seatKey, "RESERVED", 5, TimeUnit.MINUTES);
            return true;
        }

        return false;
    }

    //결제 완료 시 DB 반영
    @Transactional
    public void finalizeReservation(Long seatId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new RuntimeException("좌석 없음"));
        seat.reserve(); // 실제 DB 반영
    }

}
