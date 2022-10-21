package com.study.ioc.exception;

public class ProcessPostConstructException extends RuntimeException {
    public ProcessPostConstructException(String message, Throwable cause) {
        super(message, cause);
    }
}
