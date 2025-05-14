package org.example.backend.domain.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class KafkaTestController {

    private final KafkaProducerService producerService;

    @PostMapping("/send")
    public String send(@RequestParam String message) {
        producerService.sendMessage("test-topic", message);
        return "Message sent: " + message;
    }
}