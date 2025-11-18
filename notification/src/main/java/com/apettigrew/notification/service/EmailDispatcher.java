package com.apettigrew.notification.service;

import com.apettigrew.notification.config.ResendConfigurationProperties;
import com.resend.core.exception.ResendException;
import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@AllArgsConstructor
@Service
@EnableConfigurationProperties(ResendConfigurationProperties.class)
public class EmailDispatcher {
    private static final String EMAIL_ENDPOINT = "mail/send";

    private final Resend resend;

    private final ResendConfigurationProperties sendGridConfigurationProperties;

    public void dispatchEmail(String email, String clientName) throws IOException, ResendException {
        sendRequest(email);
    }
    private void sendRequest(String emailAddress) throws IOException, ResendException {

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from(sendGridConfigurationProperties.getFromEmail())
                .to(emailAddress)
                .subject("Invoice Created!")
                .html("<p>Invoice was successfully created</p>")
                .build();
        try{
            CreateEmailResponse data = resend.emails().send(params);
        }catch(ResendException e){
            throw new RuntimeException(e);
        }
    }

}
