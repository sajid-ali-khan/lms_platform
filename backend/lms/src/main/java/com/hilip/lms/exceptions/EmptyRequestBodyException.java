package com.hilip.lms.exceptions;

public class EmptyRequestBodyException extends RuntimeException {
    public EmptyRequestBodyException() {
        super("Request body cannot be empty.");
    }

    public EmptyRequestBodyException(String message) {
        super(message);
    }
}
