package com.apettigrew.notification.config;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Setter
@Getter
@Validated
@ConfigurationProperties(prefix = "com.notification.resend")
public class ResendConfigurationProperties {
    @NotBlank
    private String apiKey;

    @Email
    @NotBlank
    private String fromEmail;

    @NotBlank
    private String fromName;

//    @Valid
//    private DynamicTemplate dynamicTemplate = new DynamicTemplate();
//
//    @Getter
//    @Setter
//    public class DynamicTemplate {
//
//        @NotBlank(message = "Template-id must be configured")
//        @Pattern(regexp = "^d-[a-f0-9]{32}$", message = "Invalid template ID format")
//        private String templateId;
//    }
}