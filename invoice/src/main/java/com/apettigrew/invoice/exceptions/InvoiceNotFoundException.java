package com.apettigrew.invoice.exceptions;

public class InvoiceNotFoundException extends RuntimeException {
    private String message;
    private Throwable cause;

    public InvoiceNotFoundException(String message,Throwable cause){
        super(message,cause);
    }

    public InvoiceNotFoundException(String message){
        super(message);
    }

    public InvoiceNotFoundException(Throwable cause){
        super(cause);
    }
}
