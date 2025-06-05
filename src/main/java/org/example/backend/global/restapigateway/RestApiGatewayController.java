package org.example.backend.global.restapigateway;

import org.example.backend.global.config.RestApiGatewayConfig;
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
    public Map<String, String> getGatewayUrl() {
        return Map.of("restApiGatewayUrl", config.getUrl());
    }
}
