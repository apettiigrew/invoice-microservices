package com.apettigrew.user.exceptions;

public class NotFoundException extends RuntimeException {
    private String message;
    private Throwable cause;

    public NotFoundException(String message,Throwable cause){
        super(message,cause);
    }

    public NotFoundException(String message){
        super(message);
    }

    public NotFoundException(Throwable cause){
        super(cause);
    }
}
