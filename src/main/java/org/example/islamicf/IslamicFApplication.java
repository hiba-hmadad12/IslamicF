package org.example.islamicf;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;



@SpringBootApplication
@EnableScheduling
public class IslamicFApplication {

    public static void main(String[] args) {
        SpringApplication.run(IslamicFApplication.class, args);
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
   
}
