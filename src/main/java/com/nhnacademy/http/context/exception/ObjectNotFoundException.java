package com.nhnacademy.http.context.exception;

public class ObjectNotFoundException extends RuntimeException {

    public ObjectNotFoundException(String message) {
        super(String.format("Object Not Found : %s", message));
    }
}
