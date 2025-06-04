package org.example.backend.domain.reservation.batch;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationBatchProcessor {

    private final RedissonClient redissonClient;
    private final ReservationBatchService reservationBatchService;

    @Scheduled(fixedRate = 5000)
    public void processReservationQueue() {
        RQueue<String> queue = redissonClient.getQueue("reservation:queue");

        String json;
        while ((json = queue.poll()) != null) {
            reservationBatchService.process(json);
        }
    }
}
