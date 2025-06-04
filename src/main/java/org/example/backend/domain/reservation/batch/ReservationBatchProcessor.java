package org.example.backend.domain.reservation.batch;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationBatchProcessor {

    private final RedissonClient redissonClient;
    private final ReservationBatchService reservationBatchService;

    @Scheduled(fixedRate = 5000)
    public void processReservationQueue() {
        RQueue<String> queue = redissonClient.getQueue("reservation:queue");

        log.info("🌀 예약 배치 시작");

        String json;
        int count = 0;

        while ((json = queue.poll()) != null) {
            log.info("📦 처리할 메시지: {}", json);
            try {
                reservationBatchService.process(json);
                count++;
            } catch (Exception e) {
                log.error("❌ 메시지 처리 실패: {}", e.getMessage(), e);
            }
        }

        if (count == 0) {
            log.info("📭 처리할 메시지가 없습니다.");
        } else {
            log.info("✅ 총 {}건의 메시지 처리 완료", count);
        }

        log.info("🏁 예약 배치 종료");
    }
}
