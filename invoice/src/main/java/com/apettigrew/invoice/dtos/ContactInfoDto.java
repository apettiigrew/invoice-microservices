package com.apettigrew.invoice.dtos;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "invoice")
public record ContactInfoDto(String message, Map<String, String> contactDetails, List<String> onCallSupport) {

}
