package org.example.backend.global.config;


import com.siot.IamportRestClient.IamportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IamportConfig {

    @Bean
    public IamportClient iamportClient() {
        return new IamportClient("8188752611441763",
                "aKEIP08BMJcaHltoTZKK5YluO56KbvVPLKNGXTzhDZr8iq8avAeAn7qi1KN5H0t8QATio0yXLQtg9ITQ");
    }
}
