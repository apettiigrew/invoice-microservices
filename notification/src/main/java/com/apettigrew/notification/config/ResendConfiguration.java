package com.apettigrew.notification.config;

import com.resend.Resend;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@AllArgsConstructor
@Configuration
@EnableConfigurationProperties(ResendConfigurationProperties.class)
class ResendConfiguration {
    private final ResendConfigurationProperties resendConfigurationProperties;

    @Bean
    public Resend sendGrid() {
        String apiKey = resendConfigurationProperties.getApiKey();
        return new Resend(apiKey);
    }

//    @Bean
//    public Email fromEmail() {
//        String fromEmail = resendConfigurationProperties.getFromEmail();
//        String fromName = resendConfigurationProperties.getFromName();
//        return new Email(fromEmail, fromName);
//    }
}