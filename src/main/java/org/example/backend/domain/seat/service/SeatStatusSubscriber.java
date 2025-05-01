package org.example.backend.domain.seat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.backend.domain.seat.dto.SeatStatusMessage;

@Component
public class SeatStatusSubscriber {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private StringRedisTemplate redisTemplate;

    public void onMessage(String message, String channel) {
        try {
            SeatStatusMessage seatStatus = objectMapper.readValue(message, SeatStatusMessage.class);

            String key = String.format(
                    "seat:%d:%s:%s",
                    seatStatus.getPerformId(),
                    seatStatus.getSeatSection(),
                    seatStatus.getSeatNum()
            );

            String value;
            if (seatStatus.isSeatReserved()) {
                value = "RESERVED";
            } else {
                value = "AVAILABLE";
            }

            redisTemplate.opsForValue().set(key, value); // Redis에 저장

            System.out.printf(
                    "[Pub/Sub 수신] 공연ID: %d, 구역: %s, 좌석번호: %s, 예약됨: %s → 저장됨%n",
                    seatStatus.getPerformId(),
                    seatStatus.getSeatSection(),
                    seatStatus.getSeatNum(),
                    value
            );

        } catch (Exception e) {
            System.err.println("메시지 파싱 실패: " + e.getMessage());
        }
    }
}
