package com.study.ioc.processor;

import com.study.ioc.entity.Bean;

public interface BeanPostProcessor {
    Bean postProcessBeforeInitialization(String beanName, Bean bean);

    Bean postProcessAfterInitialization(String beanName, Bean bean);
}