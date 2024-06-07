package com.bondarenko.ioc.context.impl;

import com.bondarenko.ioc.annotation.Order;
import com.bondarenko.ioc.context.GenericApplicationContext;
import com.bondarenko.ioc.entity.Bean;
import com.bondarenko.ioc.entity.BeanDefinition;
import com.bondarenko.ioc.processor.AutowiredBeanPostProcessor;
import com.bondarenko.ioc.util.reader.BeanDefinitionReader;
import com.bondarenko.ioc.util.reader.impl.AnnotationBeanDefinitionReader;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Setter
@Getter
@NoArgsConstructor
public class AnnotationBasedApplicationContext extends GenericApplicationContext {

    private Map<String, Bean> beanMap = new HashMap<>();

    public AnnotationBasedApplicationContext(String... paths) {
        this(new AnnotationBeanDefinitionReader(paths));
    }

    public AnnotationBasedApplicationContext(BeanDefinitionReader definitionReader) {
        Map<String, BeanDefinition> beanDefinitions = definitionReader.getBeanDefinition();
        initContext(beanDefinitions);
    }

    @Override
    protected void initContext(Map<String, BeanDefinition> beanDefinitions) {
        createBeanPostProcessors(beanDefinitions);
        beanPostProcessorsMap.put(AutowiredBeanPostProcessor.class.getSimpleName(),
                new Bean("autowiredBeanPostProcessor", new AutowiredBeanPostProcessor(groupedBeansByClass)));

        processBeanDefinitions(beanDefinitions);
        beanMap = createBeans(beanDefinitions);

        injectValueDependencies(beanDefinitions, beanMap);

        processBeansBeforeInitialization(beanMap);
        initializeBeans(beanMap);
        processBeansAfterInitialization(beanMap);
    }

    @Override
    public List<Bean> getBeanPostProcessors(Map<String, Bean> beanPostProcessorsMap) {
        return beanPostProcessorsMap.values().stream()
                .sorted(Comparator.comparingInt(this::getOrder))
                .toList();
    }

    private int getOrder(Bean bean) {
        Order annotation = bean.getValue().getClass().getAnnotation(Order.class);
        return Objects.nonNull(annotation) ? annotation.value() : Integer.MAX_VALUE;
    }
}