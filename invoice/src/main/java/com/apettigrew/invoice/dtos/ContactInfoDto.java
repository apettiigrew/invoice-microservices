package com.apettigrew.invoice.dtos;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "invoices")
@Getter
@Setter
public class ContactInfoDto {

    private String message;
    private Map<String, String> contactDetails;
    private List<String> onCallSupport;

}
