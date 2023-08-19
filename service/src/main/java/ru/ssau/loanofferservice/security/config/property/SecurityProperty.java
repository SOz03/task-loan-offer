package ru.ssau.loanofferservice.security.config.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "loan-offer-service.security")
public class SecurityProperty {

    private Long tokenLifetime;
    private String secretKey;

}
