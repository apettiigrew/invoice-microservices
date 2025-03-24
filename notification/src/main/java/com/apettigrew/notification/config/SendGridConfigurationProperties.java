package com.apettigrew.notification.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Setter
@Getter
@Validated
@ConfigurationProperties(prefix = "com.notification.sendgrid")
public class SendGridConfigurationProperties {
    @NotBlank
    @Pattern(regexp = "^SG[0-9a-zA-Z._]{67}$")
    private String apiKey;

    @Email
    @NotBlank
    private String fromEmail;

    @NotBlank
    private String fromName;

    @Valid
    private HydrationAlertNotification hydrationAlertNotification = new HydrationAlertNotification();

    @Getter
    @Setter
    public class HydrationAlertNotification {

        @NotBlank(message = "Template-id must be configured")
        @Pattern(regexp = "^d-[a-f0-9]{32}$", message = "Invalid template ID format")
        private String templateId;
    }
}