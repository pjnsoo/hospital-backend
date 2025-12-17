package org.hospital.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Value("${server.port}")
    int port;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")                  // 모든 API 허용
                        .allowedOrigins(
                                "http://localhost:3000", // 로컬로컬
                                "https://hospital.jnsoo-pp.workers.dev", // 로컬로컬
                                "https://jnsoo.iptime.org:34000", // 로컬로컬
                                "http://jnsoo.iptime.org:34000" // 로컬로컬
                        )  // 개발/운영
                        .allowedMethods("*")               // GET, POST, PUT, DELETE ...
                        .allowCredentials(true);          // 쿠키 허용

                // 1. Jasypt 암호화 API: 오직 localhost 8080만 허용 (보안 강화)
                registry.addMapping("/local/*")
                        .allowedOrigins("http://localhost:" + port)
                        .allowedMethods("POST");
            }
        };
    }
}