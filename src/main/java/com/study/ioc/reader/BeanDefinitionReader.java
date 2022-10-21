package com.study.ioc.reader;

import com.study.ioc.entity.BeanDefinition;

import java.util.Map;

public interface BeanDefinitionReader {
    Map<String, BeanDefinition> getBeanDefinition();
}
