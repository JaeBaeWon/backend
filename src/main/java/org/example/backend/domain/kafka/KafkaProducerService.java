//package org.example.backend.domain.kafka;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Service;
//
//@RequiredArgsConstructor
//@Service
//public class KafkaProducerService {
//
//    private final KafkaTemplate<String, String> kafkaTemplate;
//
//    public void sendMessage(String topic, String message) {
//        kafkaTemplate.send(topic, message);
//    }
//}