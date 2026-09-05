package com.bonkers.maintenance_system.exception;

public class UnathorizedExceptionHandler extends RuntimeException {
    public UnathorizedExceptionHandler(String message) {
        super(message);
    }
}