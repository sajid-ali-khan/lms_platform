package com.hilip.lms.exceptions;

public class EmptyRequestBodyException extends RuntimeException {
    public EmptyRequestBodyException() {
        super("Request body is cannot be empty");
    }
}
