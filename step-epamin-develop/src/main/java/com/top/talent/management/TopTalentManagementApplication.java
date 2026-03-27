package com.top.talent.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
@EnableFeignClients
@EnableRetry
@EnableScheduling
public class TopTalentManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(TopTalentManagementApplication.class, args);
    }

}
