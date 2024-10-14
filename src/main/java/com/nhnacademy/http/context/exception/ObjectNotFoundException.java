package com.nhnacademy.http.context.exception;

public class ObjectNotFoundException extends RuntimeException {

    public ObjectNotFoundException(String name) {
        super(String.format("Object Not Found : %s", name));
    }
}
