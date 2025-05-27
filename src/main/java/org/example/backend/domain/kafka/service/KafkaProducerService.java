//package org.example.backend.domain.kafka.service;
//
//import lombok.RequiredArgsConstructor;
//import org.example.backend.domain.kafka.dto.ReservationKafkaMessage;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class KafkaProducerService {
//
//    private final KafkaTemplate<String, ReservationKafkaMessage> kafkaTemplate;
//
//    @Value("${kafka.topic.reservation}") // 예: reservation-payment
//    private String topicName;
//
//    public void sendReservationMessage(ReservationKafkaMessage message) {
//        kafkaTemplate.send(topicName, message.getTicketId(), message);
//    }
//}
