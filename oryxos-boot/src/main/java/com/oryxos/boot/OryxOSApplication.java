package com.oryxos.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * OryxOS Application - Main entry point for Spring Boot.
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.oryxos")
@EntityScan(basePackages = "com.oryxos.storage")
@EnableJpaRepositories(basePackages = "com.oryxos.storage")
public class OryxOSApplication {

    public static void main(String[] args) {
        SpringApplication.run(OryxOSApplication.class, args);
    }
}
