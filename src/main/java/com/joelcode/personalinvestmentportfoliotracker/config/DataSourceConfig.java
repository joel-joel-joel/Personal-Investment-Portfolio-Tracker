package com.joelcode.personalinvestmentportfoliotracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;

import javax.sql.DataSource;

// Disable this manual DataSource configuration by default to allow Spring Boot auto-configuration.
// Activate only with 'legacy' profile if explicitly needed.
@Configuration
@Profile("legacy")
public class DataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }
}
