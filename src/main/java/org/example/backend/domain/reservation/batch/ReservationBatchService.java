package org.example.backend.domain.reservation.batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.example.backend.domain.payment.entity.PayType;
import org.example.backend.domain.payment.entity.Payment;
import org.example.backend.domain.payment.entity.PaymentStatus;
import org.example.backend.domain.payment.repository.PaymentRepository;
import org.example.backend.domain.performance.entity.Performance;
import org.example.backend.domain.performance.repository.PerformanceRepository;
import org.example.backend.domain.reservation.dto.ReservationMessageDto;
import org.example.backend.domain.reservation.entity.Reservation;
import org.example.backend.domain.reservation.entity.ReservationStatus;
import org.example.backend.domain.reservation.repository.ReservationRepository;
import org.example.backend.domain.seat.entity.Seat;
import org.example.backend.domain.seat.repository.SeatRepository;
import org.example.backend.domain.user.entity.User;
import org.example.backend.domain.user.repository.UserRepository;
import org.example.backend.global.exception.CustomException;
import org.example.backend.global.exception.ExceptionContent;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationBatchService {

    private final ObjectMapper objectMapper;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final PerformanceRepository performanceRepository;
    private final SeatRepository seatRepository;
    private final RedissonClient redissonClient;

    @Transactional
    public void process(String json) {
        try {
            ReservationMessageDto dto = objectMapper.readValue(json, ReservationMessageDto.class);

            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new CustomException(ExceptionContent.NOT_FOUND_USER));
            Performance performance = performanceRepository.findById(dto.getPerformanceId())
                    .orElseThrow(() -> new CustomException(ExceptionContent.NOT_FOUND_PERFORMANCE));
            Seat seat = seatRepository.findById(dto.getSeatId())
                    .orElseThrow(() -> new CustomException(ExceptionContent.NOT_FOUND_SEAT));

            Reservation reservation = Reservation.builder()
                    .user(user)
                    .performance(performance)
                    .seat(seat)
                    .reservationStatus(ReservationStatus.RESERVED)
                    .ticketId(dto.getTicketId())
                    .build();
            reservationRepository.save(reservation);

            Payment payment = Payment.builder()
                    .impUid(dto.getImpUid())
                    .merchantUid(dto.getMerchantUid())
                    .paymentAmount(dto.getPaymentAmount())
                    .paymentStatus(PaymentStatus.SUCCESS)
                    .payType(PayType.valueOf(dto.getPayMethod().toUpperCase()))
                    .paymentDate(new Date())
                    .reservation(reservation)
                    .build();
            paymentRepository.save(payment);

            String key = "reservation:pending:" + dto.getUserId() + ":" + dto.getSeatId();
            redissonClient.getBucket(key).set("CONFIRMED", 1, TimeUnit.HOURS);

        } catch (Exception e) {
            e.printStackTrace(); // 또는 로그 처리
        }
    }
}
