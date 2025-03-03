package com.apettigrew.invoice.jsonapi;

import com.apettigrew.invoice.ResourceTypes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

@Getter
@ToString
@AllArgsConstructor
public class InvoiceResource implements Resource<com.apettigrew.invoice.dtos.InvoiceDto> {
    private final String type = ResourceTypes.INVOICES;
    private Long id;
    private com.apettigrew.invoice.dtos.InvoiceDto attributes;

    public static InvoiceResource toResource(final com.apettigrew.invoice.entities.Invoice invoice){
        if(invoice == null){
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        final var modelMapper = new ModelMapper();
        final var attributes = modelMapper.map(invoice, com.apettigrew.invoice.dtos.InvoiceDto.class);
        try {
            attributes.setClientAddress(objectMapper.readValue(invoice.getClientAddress(), com.apettigrew.invoice.dtos.AddressDto.class));
            attributes.setSenderAddress(objectMapper.readValue(invoice.getSenderAddress(), com.apettigrew.invoice.dtos.AddressDto.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return new InvoiceResource(invoice.getId(),attributes);
    }
}
