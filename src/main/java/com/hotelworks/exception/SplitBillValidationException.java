package com.hotelworks.exception;

public class SplitBillValidationException extends RuntimeException {
    
    public SplitBillValidationException(String message) {
        super(message);
    }
    
    public SplitBillValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}