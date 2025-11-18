package com.apettigrew.notification.config;

import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.objects.Email;
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
    public SendGrid sendGrid() {
        String apiKey = resendConfigurationProperties.getApiKey();
        return new SendGrid(apiKey);
    }

    @Bean
    public Email fromEmail() {
        String fromEmail = resendConfigurationProperties.getFromEmail();
        String fromName = resendConfigurationProperties.getFromName();
        return new Email(fromEmail, fromName);
    }
}