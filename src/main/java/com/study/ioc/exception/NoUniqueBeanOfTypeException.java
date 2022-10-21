package com.study.ioc.exception;

public class NoUniqueBeanOfTypeException extends RuntimeException {

    public NoUniqueBeanOfTypeException(String message) {
        super(message);
    }

}
