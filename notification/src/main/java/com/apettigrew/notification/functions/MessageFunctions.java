package com.apettigrew.notification.functions;

import com.apettigrew.notification.dto.InvoiceDto;
import com.apettigrew.notification.service.EmailDispatcher;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.function.Function;

@AllArgsConstructor
@Configuration
public class MessageFunctions {
    private static final Logger log = LoggerFactory.getLogger(MessageFunctions.class);
    private final EmailDispatcher emailDispatcher;

    @Bean
    public Function<InvoiceDto,InvoiceDto> email() {
        return invoiceDto -> {
            try {
                emailDispatcher.dispatchHydrationAlert(invoiceDto.getClientEmail(),invoiceDto.getClientName());
                log.info("Sending email with the details : " +  invoiceDto.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return invoiceDto;
        };
    }
}
