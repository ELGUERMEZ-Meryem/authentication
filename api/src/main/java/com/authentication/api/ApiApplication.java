package com.authentication.api;

import com.authentication.api.security.AuditorAwareImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class ApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }

    //The auditing infrastructure will pick up this bean automatically and use it to determine the current user
    @Bean
    AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }

}
