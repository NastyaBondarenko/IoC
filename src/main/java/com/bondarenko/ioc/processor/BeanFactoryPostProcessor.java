package com.bondarenko.ioc.processor;

import com.bondarenko.ioc.entity.BeanDefinition;

import java.util.Map;

public interface BeanFactoryPostProcessor {

    void postProcessBeanFactory(Map<String, BeanDefinition> beanDefinitionsMap);
}