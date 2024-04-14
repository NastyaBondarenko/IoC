package com.bondarenko.ioc.util.reader;

import com.bondarenko.ioc.entity.BeanDefinition;

import java.util.Map;

public interface BeanDefinitionReader {
    Map<String, BeanDefinition> getBeanDefinition();
}
