package ru.ssau.loanofferservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.ssau.loanofferservice.security.config.property.SecurityProperty;

@SpringBootApplication
@EnableConfigurationProperties({SecurityProperty.class})
public class LoanOfferApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoanOfferApplication.class, args);
    }

}
