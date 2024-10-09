package com.bondarenko.ioc.exception;

public class EventHandlerException extends RuntimeException {

    public EventHandlerException(String message, Throwable cause) {
        super(message, cause);
    }
}