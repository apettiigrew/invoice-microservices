package com.apettigrew.invoice.validation;

import com.apettigrew.invoice.enums.InvoiceStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidInvoiceStatusValidator implements ConstraintValidator<ValidInvoiceStatus, InvoiceStatus> {
    
    @Override
    public void initialize(ValidInvoiceStatus constraintAnnotation) {
        // No initialization needed
    }
    
    @Override
    public boolean isValid(InvoiceStatus status, ConstraintValidatorContext context) {
        if (status == null) {
            return true; // Let @NotNull handle null validation
        }
        return status == InvoiceStatus.PENDING || status == InvoiceStatus.PAID;
    }
}

