package com.joelcode.personalinvestmentportfoliotracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
public class PersonalInvestmentPortfolioTrackerApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(PersonalInvestmentPortfolioTrackerApplication.class);
        app.setAdditionalProfiles("dev");   // fallback if no profile supplied
        app.run(args);
    }
}
