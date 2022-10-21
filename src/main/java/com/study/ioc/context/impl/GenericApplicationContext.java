package com.study.ioc.context.impl;

import com.study.ioc.context.ApplicationContext;
import com.study.ioc.entity.Bean;
import com.study.ioc.entity.BeanDefinition;
import com.study.ioc.reader.BeanDefinitionReader;
import com.study.ioc.reader.sax.XmlBeanDefinitionReader;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class GenericApplicationContext implements ApplicationContext {

    private Map<String, Bean> beans;

    GenericApplicationContext() {
    }

    public GenericApplicationContext(String... paths) {
        this(new XmlBeanDefinitionReader(paths));
    }

    public GenericApplicationContext(BeanDefinitionReader definitionReader) {
        Map<String, BeanDefinition> beanDefinitions = definitionReader.getBeanDefinition();

        beans = createBeans(beanDefinitions);
        injectValueDependencies(beanDefinitions, beans);
        injectRefDependencies(beanDefinitions, beans);
    }

    @Override
    public Object getBean(String beanId) {
        return null;
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        return null;
    }

    @Override
    public <T> T getBean(String id, Class<T> clazz) {
        return null;
    }

    @Override
    public List<String> getBeanNames() {
        return null;
    }

    Map<String, Bean> createBeans(Map<String, BeanDefinition> beanDefinitionMap) {
        return null;
    }

    void injectValueDependencies(Map<String, BeanDefinition> beanDefinitions, Map<String, Bean> beans) {
    }

    void injectRefDependencies(Map<String, BeanDefinition> beanDefinitions, Map<String, Bean> beans) {

    }

    private String getSetterName(String fieldName) {
        return "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    void injectValue(Object object, Method classMethod, String propertyValue) throws ReflectiveOperationException {
    }

    void setBeans(Map<String, Bean> beans) {
        this.beans = beans;
    }
}
