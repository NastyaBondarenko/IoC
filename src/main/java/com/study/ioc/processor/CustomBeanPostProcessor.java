package com.study.ioc.processor;

import com.study.ioc.entity.Bean;

public class CustomBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Bean postProcessBeforeInitialization(String beanName, Bean bean) {
        return bean;
    }

    @Override
    public Bean postProcessAfterInitialization(String beanName, Bean bean) {
        return bean;
    }
}