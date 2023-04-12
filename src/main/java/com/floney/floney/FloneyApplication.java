package com.floney.floney;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class FloneyApplication {

    public static void main(String[] args) {
        SpringApplication.run(FloneyApplication.class, args);
    }

}
