package com.apettigrew.invoice.jsonapi;

import com.apettigrew.invoice.ResourceTypes;
import com.apettigrew.invoice.dtos.AddressDto;
import com.apettigrew.invoice.dtos.InvoiceDto;
import com.apettigrew.invoice.dtos.InvoiceItemDto;
import com.apettigrew.invoice.entities.Invoice;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceResource implements Resource<InvoiceDto> {
    private final String type = ResourceTypes.INVOICES;
    private int id;
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

        // Map invoice items from entity to DTO
        if (invoice.getInvoiceItems() != null) {
            List<InvoiceItemDto> invoiceItemDtos = invoice.getInvoiceItems().stream()
                    .map(item -> modelMapper.map(item, InvoiceItemDto.class))
                    .collect(Collectors.toList());
            attributes.setInvoiceItems(invoiceItemDtos);
        } else {
            attributes.setInvoiceItems(new ArrayList<>());
        }

        return new InvoiceResource(invoice.getId(),attributes);
    }
}
