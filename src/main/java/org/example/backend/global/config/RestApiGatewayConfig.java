package org.example.backend.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RestApiGatewayConfig {

    @Value("${rest-api-gateway.url}")
    private String url;

    public String getUrl() {
        return url;
    }
}
