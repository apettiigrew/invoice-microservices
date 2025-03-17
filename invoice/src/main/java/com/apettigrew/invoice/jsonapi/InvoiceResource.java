package com.apettigrew.invoice.jsonapi;

import com.apettigrew.invoice.ResourceTypes;
import com.apettigrew.invoice.dtos.AddressDto;
import com.apettigrew.invoice.dtos.InvoiceDto;
import com.apettigrew.invoice.entities.Invoice;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.modelmapper.ModelMapper;

@Getter
@ToString
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceResource implements Resource<InvoiceDto> {
    private final String type = ResourceTypes.INVOICES;
    private String id;
    private InvoiceDto attributes;



    public static InvoiceResource toResource(final Invoice invoice){
        if(invoice == null){
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        final var modelMapper = new ModelMapper();
        final var attributes = modelMapper.map(invoice, InvoiceDto.class);
        try {
            attributes.setClientAddress(objectMapper.readValue(invoice.getClientAddress(), AddressDto.class));
            attributes.setSenderAddress(objectMapper.readValue(invoice.getSenderAddress(), AddressDto.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return new InvoiceResource(invoice.getId(),attributes);
    }
}
