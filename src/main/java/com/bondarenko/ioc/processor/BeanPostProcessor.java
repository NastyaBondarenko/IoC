package com.bondarenko.ioc.processor;

import com.bondarenko.ioc.entity.Bean;

import java.util.Map;

public interface BeanPostProcessor {

    Object postProcessBeforeInitialization(String beanName, Bean bean, Map<String, Bean> beanMap);

    Object postProcessAfterInitialization(String beanName, Bean bean);
}