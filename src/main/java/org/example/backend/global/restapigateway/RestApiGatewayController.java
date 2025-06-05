package org.example.backend.global.restapigateway;

import org.example.backend.global.config.RestApiGatewayConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class RestApiGatewayController {
    private final RestApiGatewayConfig properties;

    public RestApiGatewayController(RestApiGatewayConfig properties) {
        this.properties = properties;
    }

    @GetMapping("/config")
    public Map<String, String> getConfig() {
        return Map.of("restApiGatewayUrl", properties.getUrl());
    }
}

