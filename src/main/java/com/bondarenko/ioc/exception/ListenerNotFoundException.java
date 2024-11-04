package com.bondarenko.ioc.exception;

public class ListenerNotFoundException extends RuntimeException {

    public ListenerNotFoundException(String message) {
        super(message);
    }
}