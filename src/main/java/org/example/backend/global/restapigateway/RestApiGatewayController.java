package org.example.backend.global.restapigateway;

import org.example.backend.global.config.RestApiGatewayConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RestApiGatewayController {

    private final RestApiGatewayConfig config;

    public RestApiGatewayController(RestApiGatewayConfig config) {
        this.config = config;
    }

    @GetMapping("/config")
    public ResponseEntity<?> getGatewayUrl() {
        String url = config.getUrl();
        if (url == null || url.isBlank()) {
            return ResponseEntity
                    .status(500)
                    .body(Map.of("error", "REST API Gateway URL is not configured"));
        }
        return ResponseEntity.ok(Map.of("restApiGatewayUrl", url));
    }
}
