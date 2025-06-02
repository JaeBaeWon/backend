package org.example.backend.domain.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.siot.IamportRestClient.IamportClient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.backend.domain.payment.dto.request.PaymentVerificationRequest;
import org.example.backend.domain.payment.dto.response.PaymentCompleteResponse;
import org.example.backend.domain.payment.entity.PayType;
import org.example.backend.domain.payment.entity.Payment;
import org.example.backend.domain.payment.entity.PaymentStatus;
import org.example.backend.domain.payment.repository.PaymentRepository;
import org.example.backend.domain.performance.entity.Performance;
import org.example.backend.domain.performance.repository.PerformanceRepository;
import org.example.backend.domain.reservation.dto.ReservationKafkaDto;
import org.example.backend.domain.reservation.dto.ReservationResponse;
import org.example.backend.domain.reservation.entity.Reservation;
import org.example.backend.domain.reservation.entity.ReservationStatus;
import org.example.backend.domain.reservation.repository.ReservationRepository;
import org.example.backend.domain.seat.entity.Seat;
import org.example.backend.domain.seat.entity.SeatStatus;
import org.example.backend.domain.seat.repository.SeatRepository;
import org.example.backend.domain.user.entity.User;
import org.example.backend.domain.user.repository.UserRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final IamportClient iamportClient;
    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final PerformanceRepository performanceRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;


    @Transactional
    public ReservationResponse verifyAndCreateReservationAndSavePayment(PaymentVerificationRequest request) throws Exception {
        var paymentInfo = verifyPaymentWithIamport(request.getImpUid());

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        Performance performance = performanceRepository.findById(request.getPerformanceId())
                .orElseThrow(() -> new IllegalArgumentException("공연 없음"));

        Seat seat = validateSeatAvailability(request.getSeatId(), performance, paymentInfo);

        String ticketId = UUID.randomUUID().toString().replace("-", "").substring(0, 12);

        // Kafka로 전송할 DTO
        ReservationKafkaDto dto = ReservationKafkaDto.builder()
                .userId(user.getUserId())
                .performanceId(performance.getPerformanceId())
                .seatId(seat.getSeatId())
                .ticketId(ticketId)
                .impUid(request.getImpUid())
                .merchantUid(request.getMerchantUid())
                .paymentAmount(paymentInfo.getAmount().intValue())
                .payMethod(paymentInfo.getPayMethod())
                .build();

        kafkaTemplate.send("reservation", objectMapper.writeValueAsString(dto));

        // 응답용 DTO 리턴
        return ReservationResponse.builder()
                .userId(user.getUserId())
                .performanceId(performance.getPerformanceId())
                .seatId(seat.getSeatId())
                .ticketId(ticketId)
                .build();
    }


    private com.siot.IamportRestClient.response.Payment verifyPaymentWithIamport(String impUid) throws Exception {
        var response = iamportClient.paymentByImpUid(impUid);
        var payment = response.getResponse();
        if (payment == null) throw new IllegalStateException("결제 정보 없음");
        if (!"paid".equals(payment.getStatus())) throw new IllegalStateException("결제 완료 상태가 아님");
        return payment;
    }

    private Seat validateSeatAvailability(Long seatId, Performance performance, com.siot.IamportRestClient.response.Payment paymentInfo) {
        var seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("좌석 없음"));

        if (seat.getSeatStatus() != SeatStatus.AVAILABLE)
            throw new IllegalStateException("좌석 사용 불가");

        if (paymentInfo.getAmount().intValue() != performance.getPrice())
            throw new IllegalStateException("결제 금액 불일치");

        return seat;
    }

    private Reservation createReservationEntity(User user, Performance performance, Seat seat) {
        return Reservation.builder()
                .userId(user)
                .performanceId(performance)
                .seatId(seat)
                .reservationStatus(ReservationStatus.RESERVED)
                .ticketId(UUID.randomUUID().toString().replace("-", "").substring(0, 12))
                .build();
    }

    private void savePaymentRecord(PaymentVerificationRequest request,
                                   com.siot.IamportRestClient.response.Payment paymentInfo,
                                   Reservation reservation) {

        var payment = Payment.builder()
                .impUid(request.getImpUid())
                .merchantUid(request.getMerchantUid())
                .paymentAmount(paymentInfo.getAmount().intValue())
                .paymentStatus(PaymentStatus.SUCCESS)
                .payType(PayType.valueOf(paymentInfo.getPayMethod().toUpperCase()))
                .paymentDate(new Date())
                .reservation(reservation)
                .build();

        paymentRepository.save(payment);
    }



    public PaymentCompleteResponse getPaymentInfoByReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("해당 예약 정보가 없습니다."));


        Payment payment = paymentRepository.findByReservation(reservation)
                .orElseThrow(() -> new RuntimeException("해당 예약의 결제 내역 없음"));

        Performance performance = reservation.getPerformanceId();
        Seat seat = reservation.getSeatId();

        return PaymentCompleteResponse.builder()
                .ticketNumber(reservation.getTicketId())
                .performanceTitle(performance.getTitle())
                .performanceLocation(performance.getLocation())
                .performanceDate(performance.getPerformanceStartAt().toString())
                .seatInfo(seat.getSeatSection() + "구역 " + seat.getSeatNum() + "번")
                .paymentAmount(payment.getPaymentAmount())
                .payType(payment.getPayType().getDescription())
                .paymentTime(payment.getPaymentDate().toString())
                .build();
    }

}
