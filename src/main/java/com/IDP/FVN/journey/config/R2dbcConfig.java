package com.IDP.FVN.journey.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Configuration
@EnableR2dbcRepositories(basePackages = "com.IDP.FVN.journey.repository")
public class R2dbcConfig {

    // It remains empty for now!
    // Spring Boot handles the ConnectionFactory via your Docker environment variables.

}