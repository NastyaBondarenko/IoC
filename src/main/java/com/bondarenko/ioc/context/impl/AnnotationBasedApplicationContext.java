package com.bondarenko.ioc.context.impl;

import com.bondarenko.ioc.annotation.Order;
import com.bondarenko.ioc.context.GenericApplicationContext;
import com.bondarenko.ioc.entity.Bean;
import com.bondarenko.ioc.entity.BeanDefinition;
import com.bondarenko.ioc.processor.AutowiredBeanPostProcessor;
import com.bondarenko.ioc.publisher.impl.DefaultApplicationEventPublisher;
import com.bondarenko.ioc.util.reader.BeanDefinitionReader;
import com.bondarenko.ioc.util.reader.impl.AnnotationBeanDefinitionReader;
import lombok.Getter;

import java.util.Map;
import java.util.Objects;


@Getter
public class AnnotationBasedApplicationContext extends GenericApplicationContext {

    public AnnotationBasedApplicationContext(String... paths) {
        this(new AnnotationBeanDefinitionReader(paths));
    }

    public AnnotationBasedApplicationContext(BeanDefinitionReader definitionReader) {
        super(definitionReader);
    }

    @Override
    protected void initContext(Map<String, BeanDefinition> beanDefinitions) {
        beanMap.put(AutowiredBeanPostProcessor.class.getSimpleName(),
                new Bean("autowiredBeanPostProcessor", new AutowiredBeanPostProcessor(groupedBeansByClass)));
        beanMap.put(DefaultApplicationEventPublisher.class.getSimpleName(),
                new Bean("applicationEventPublisher", new DefaultApplicationEventPublisher(this)));
        super.initContext(beanDefinitions);
    }

    @Override
    public int getOrder(Bean bean) {
        Order annotation = bean.getValue().getClass().getAnnotation(Order.class);
        return Objects.nonNull(annotation) ? annotation.value() : Integer.MAX_VALUE;
    }
}