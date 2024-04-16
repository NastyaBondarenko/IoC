package com.bondarenko.ioc.processor.impl;

import com.bondarenko.ioc.entity.BeanDefinition;
import com.bondarenko.ioc.processor.BeanFactoryPostProcessor;

import java.util.Map;

public class CustomBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(Map<String, BeanDefinition> beanDefinitionsMap) {
        beanDefinitionsMap.values().stream()
                .filter(beanDefinition -> beanDefinition.getId().equals("mailServicePOP"))
                .forEach(beanDefinition -> beanDefinition.setValueDependencies(Map.of("port", "4500")));
    }
}