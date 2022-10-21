package com.study.ioc.exception;

public class PostProcessBeanFactoryException extends RuntimeException {

    public PostProcessBeanFactoryException (String message, Throwable cause) {
        super(message, cause);
    }
}