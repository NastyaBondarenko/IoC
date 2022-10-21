package com.study.ioc.exception;

public class NoSuchBeanDefinitionException extends RuntimeException {

    public NoSuchBeanDefinitionException(String id, String clazzName, String actualClazzName) {
        super("No qualifying bean of type " + clazzName + " with id " + id + " is defined. Bean is of type " + actualClazzName);
    }
}

