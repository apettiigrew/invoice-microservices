package com.apettigrew.invoice.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvoiceErrorResponse {
    private int status;
    private String message;
    private long timeStamp;
    private Map<String,String> errors;
}
