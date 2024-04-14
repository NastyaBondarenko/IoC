package com.bondarenko.ioc.processor;

import com.bondarenko.ioc.entity.Bean;

public interface BeanPostProcessor {

    Object postProcessBeforeInitialization(String beanName, Bean bean);

    Object postProcessAfterInitialization(String beanName, Bean bean);
}