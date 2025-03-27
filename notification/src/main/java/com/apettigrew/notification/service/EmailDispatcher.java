package com.apettigrew.notification.service;

import com.apettigrew.notification.config.DynamicTemplatePersonalization;
import com.apettigrew.notification.config.SendGridConfigurationProperties;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@AllArgsConstructor
@Service
@EnableConfigurationProperties(SendGridConfigurationProperties.class)
public class EmailDispatcher {
    private static final String EMAIL_ENDPOINT = "mail/send";

    private final SendGrid sendGrid;
    private final Email fromEmail;
    private final SendGridConfigurationProperties sendGridConfigurationProperties;

    public void dispatchEmail(String emailId, String subject, String body) throws IOException {
        Email toEmail = new Email(emailId);
        Content content = new Content("text/plain", body);
        Mail mail = new Mail(fromEmail, subject, toEmail, content);

        sendRequest(mail);
    }

    public void dispatchHydrationAlert(String emailId, String username) throws IOException {
        Email toEmail = new Email(emailId);
        String templateId = sendGridConfigurationProperties.getDynamicTemplate().getTemplateId();

        DynamicTemplatePersonalization personalization = new DynamicTemplatePersonalization();
        personalization.add("clientName", username);
        personalization.addTo(toEmail);

        Mail mail = new Mail();
        mail.setFrom(fromEmail);
        mail.setTemplateId(templateId);
        mail.addPersonalization(personalization);

        sendRequest(mail);
    }

    private void sendRequest(Mail mail) throws IOException {
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint(EMAIL_ENDPOINT);
        request.setBody(mail.build());

        Response response = sendGrid.api(request);
        log.info("Sendgrid email status code:", response.getStatusCode());
        log.info("Sendgrid email body content:", response.getBody());
    }

}
