package com.joelcode.personalinvestmentportfoliotracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.joelcode.personalinvestmentportfoliotracker.repositories")
public class PersonalInvestmentPortfolioTrackerApplication {

    @GetMapping

    public static void main(String[] args) {
        SpringApplication.run(PersonalInvestmentPortfolioTrackerApplication.class, args);
    }

}
