package org.example.backend.domain.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.backend.domain.payment.entity.PayType;
import org.example.backend.domain.payment.entity.Payment;
import org.example.backend.domain.payment.entity.PaymentStatus;
import org.example.backend.domain.payment.repository.PaymentRepository;
import org.example.backend.domain.performance.entity.Performance;
import org.example.backend.domain.performance.repository.PerformanceRepository;
import org.example.backend.domain.reservation.dto.ReservationKafkaDto;
import org.example.backend.domain.reservation.entity.Reservation;
import org.example.backend.domain.reservation.entity.ReservationStatus;
import org.example.backend.domain.reservation.repository.ReservationRepository;
import org.example.backend.domain.seat.entity.Seat;
import org.example.backend.domain.seat.entity.SeatStatus;
import org.example.backend.domain.seat.repository.SeatRepository;
import org.example.backend.domain.user.entity.User;
import org.example.backend.domain.user.repository.UserRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final ObjectMapper objectMapper;

    private final UserRepository userRepository;
    private final PerformanceRepository performanceRepository;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;

    /*@KafkaListener(topics = "test-topic", groupId = "test-group")
    public void listen(String message) {
        System.out.println("Consumed message: " + message);
    }
*/
    @KafkaListener(
            topics = "reservation",
            groupId = "reservation-group",
            autoStartup = "${spring.kafka.enabled:false}"
    )
    @Transactional  // 메시지 처리 중 실패하면 전체 롤백
    public void consumeReservation(ConsumerRecord<String, String> record) {
        try {
            String message = record.value();
            ReservationKafkaDto dto = objectMapper.readValue(message, ReservationKafkaDto.class);

            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
            Performance performance = performanceRepository.findById(dto.getPerformanceId())
                    .orElseThrow(() -> new IllegalArgumentException("공연 없음"));
            Seat seat = seatRepository.findById(dto.getSeatId())
                    .orElseThrow(() -> new IllegalArgumentException("좌석 없음"));

            if (seat.getSeatStatus() != SeatStatus.AVAILABLE) {
                throw new IllegalStateException("이미 예약된 좌석: " + dto.getSeatId());
            }

            // 좌석 BOOKED 처리
            seat.updateStatus(SeatStatus.BOOKED);
            seatRepository.save(seat);

            // Reservation 저장
            Reservation reservation = Reservation.builder()
                    .user(user)
                    .performance(performance)
                    .seat(seat)
                    .ticketId(dto.getTicketId())
                    .reservationStatus(ReservationStatus.RESERVED)
                    .build();
            reservationRepository.save(reservation);

            // Payment 저장
            Payment payment = Payment.builder()
                    .reservation(reservation)
                    .impUid(dto.getImpUid())
                    .merchantUid(dto.getMerchantUid())
                    .payType(PayType.valueOf(dto.getPayMethod().toUpperCase()))
                    .paymentAmount(dto.getPaymentAmount())
                    .paymentDate(new Date())
                    .paymentStatus(PaymentStatus.SUCCESS)
                    .build();

            paymentRepository.save(payment);

            System.out.println("🎟 예매 및 결제 저장 완료 (userId=" + user.getUserId() + ")");

        } catch (Exception e) {
            System.err.println("❌ Kafka Consumer 처리 실패: " + e.getMessage());
            throw new RuntimeException(e);  // Kafka가 메시지를 재시도하도록 예외 재발생
        }
    }
}