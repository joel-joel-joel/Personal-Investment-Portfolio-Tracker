package com.joelcode.personalinvestmentportfoliotracker;

import com.joelcode.personalinvestmentportfoliotracker.logging.BetterStackLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Optional;

@SpringBootApplication
@EnableScheduling
public class PersonalInvestmentPortfolioTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersonalInvestmentPortfolioTrackerApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(@Autowired(required = false) BetterStackLogger logger) {
        return args -> {
            if (logger != null) {
                logger.info("Backend started successfully!");
            } else {
                System.out.println("✓ Backend started successfully!");
            }
        };
    }
}
