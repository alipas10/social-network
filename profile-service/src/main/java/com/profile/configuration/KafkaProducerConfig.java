package com.profile.configuration;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Configuration
public class KafkaProducerConfig {

    @NonFinal
    @Value("${kafka.bootstrap-servers}")
    String bootstrapServer;

    public Map<String, Object> config(){
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(ProducerConfig.RETRIES_CONFIG, "5");
        config.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, "700");
        return  config;
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        ProducerFactory producerFactory = new DefaultKafkaProducerFactory(config());
        return new KafkaTemplate<>(producerFactory);
    }
}
