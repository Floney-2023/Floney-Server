package com.floney.floney.common.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WebConfig {

    @Bean
    public RestTemplate restTemplate(final RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }
}
