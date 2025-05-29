package org.example.backend.global.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.net.InetAddress;

@RestController
public class HealthCheckController {

    @GetMapping("/health")
    public String health() {
        try {
            String podName = InetAddress.getLocalHost().getHostName();
            return "OK from " + podName;
        } catch (Exception e) {
            return "OK from unknown pod";
        }
    }
}
