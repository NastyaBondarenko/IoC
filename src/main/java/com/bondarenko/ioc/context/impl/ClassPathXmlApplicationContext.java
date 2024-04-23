package com.bondarenko.ioc.context.impl;

import com.bondarenko.ioc.context.GenericApplicationContext;
import com.bondarenko.ioc.entity.BeanDefinition;
import com.bondarenko.ioc.util.reader.BeanDefinitionReader;
import com.bondarenko.ioc.util.reader.impl.XmlBeanDefinitionReader;
import lombok.Getter;

import java.util.Map;


@Getter
public class ClassPathXmlApplicationContext extends GenericApplicationContext {

    ClassPathXmlApplicationContext() {
    }

    public ClassPathXmlApplicationContext(String... paths) {
        this(new XmlBeanDefinitionReader(paths));
    }

    public ClassPathXmlApplicationContext(BeanDefinitionReader definitionReader) {
        Map<String, BeanDefinition> beanDefinitions = definitionReader.getBeanDefinition();
        initContext(beanDefinitions);
    }
}