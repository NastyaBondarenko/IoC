package com.study.ioc.processor;

import com.study.ioc.entity.Bean;

public interface BeanPostProcessor {
    Object postProcessBeforeInitialization(String beanName, Bean bean);

    Object postProcessAfterInitialization(String beanName, Bean bean);
}