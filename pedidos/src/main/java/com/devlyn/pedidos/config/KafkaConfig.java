package com.devlyn.pedidos.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    @Value("${icompras.config.kafka.server-url}")
    private String kafkaServerUrl;

}
