package ru.ssau.loanofferservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource({"classpath:application-test.yml", "classpath:logback-test.xml"})
class LoanOfferApplicationTest {

    @Test
    void contextLoads() {

    }

}