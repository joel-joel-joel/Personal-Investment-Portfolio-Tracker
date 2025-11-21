package com.joelcode.personalinvestmentportfoliotracker.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Bean;
import org.springframework.modulith.events.core.EventPublicationRepository;
import org.springframework.modulith.events.jpa.JpaEventPublicationRepository;

@Configuration
public class ModulithConfig {

    @Bean
    @Primary
    public EventPublicationRepository eventPublicationRepository(JpaEventPublicationRepository jpaRepo) {
        return jpaRepo;
    }
}
