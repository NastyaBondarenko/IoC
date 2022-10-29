package com.study.ioc.processor;

import com.study.ioc.entity.BeanDefinition;

import java.util.Map;

public class CustomBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(Map<String, BeanDefinition> beanDefinitionsMap) {
        beanDefinitionsMap.values().stream()
                .filter(beanDefinition -> beanDefinition.getId().equals("mailServicePOP"))
                .forEach(beanDefinition -> beanDefinition.setValueDependencies(Map.of("port", "4500")));
    }
}