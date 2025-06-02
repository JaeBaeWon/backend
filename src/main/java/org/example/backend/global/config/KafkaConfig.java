package org.example.backend.global.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true")
public class KafkaConfig {

    @Value("${KAFKA_SERVER}")
    private String bootstrapServers;

    @Value("${KAFKA_USERNAME}")
    private String kafkaUsername;

    @Value("${KAFKA_PASSWORD}")
    private String kafkaPassword;

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();

        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        // SCRAM 인증 설정
        configProps.put("security.protocol", "SASL_SSL");
        configProps.put("sasl.mechanism", "SCRAM-SHA-512");
        configProps.put("sasl.jaas.config", String.format(
                "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";",
                kafkaUsername,
                kafkaPassword));

        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.RETRIES_CONFIG, 10);
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}

// package org.example.backend.global.config;
//
// import org.apache.kafka.clients.producer.ProducerConfig;
// import org.apache.kafka.common.serialization.StringSerializer;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.kafka.core.DefaultKafkaProducerFactory;
// import org.springframework.kafka.core.KafkaTemplate;
// import org.springframework.kafka.core.ProducerFactory;
//
// import java.util.HashMap;
// import java.util.Map;
//
// @Configuration
// public class KafkaConfig {
//
// @Value("${spring.kafka.bootstrap-servers}")
// private String bootstrapServers;
//
// @Value("${KAFKA_USERNAME}")
// private String kafkaUsername;
//
// @Value("${KAFKA_PASSWORD}")
// private String kafkaPassword;
//
// @Bean
// public ProducerFactory<String, String> producerFactory() {
// Map<String, Object> configProps = new HashMap<>();
//
// configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
// configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
// StringSerializer.class);
// configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
// StringSerializer.class);
//
// // SCRAM 인증 설정
// configProps.put("security.protocol", "SASL_SSL");
// configProps.put("sasl.mechanism", "SCRAM-SHA-512");
// configProps.put("sasl.jaas.config", String.format(
// "org.apache.kafka.common.security.scram.ScramLoginModule required
// username=\"%s\" password=\"%s\";",
// kafkaUsername,
// kafkaPassword
// ));
//
// // 권장 설정
// configProps.put(ProducerConfig.ACKS_CONFIG, "all");
// configProps.put(ProducerConfig.RETRIES_CONFIG, 10);
// configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
//
// return new DefaultKafkaProducerFactory<>(configProps);
// }
//
// @Bean
// public KafkaTemplate<String, String> kafkaTemplate() {
// return new KafkaTemplate<>(producerFactory());
// }
// }