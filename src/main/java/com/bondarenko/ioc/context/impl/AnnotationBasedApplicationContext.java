package com.bondarenko.ioc.context.impl;

import com.bondarenko.ioc.context.GenericApplicationContext;
import com.bondarenko.ioc.entity.Bean;
import com.bondarenko.ioc.entity.BeanDefinition;
import com.bondarenko.ioc.util.reader.BeanDefinitionReader;
import com.bondarenko.ioc.util.reader.impl.AnnotationBeanDefinitionReader;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

@Setter
@Getter
@NoArgsConstructor
public class AnnotationBasedApplicationContext extends GenericApplicationContext {

    private static final String ANNOTATION_ORDER = "Order";
    private static final String ORDER_VALUE = "value";
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
        List<Annotation> annotations = Arrays.asList(bean.getValue().getClass().getAnnotations());
        return annotations.stream()
                .filter(annotation -> annotation.annotationType().getSimpleName().equals(ANNOTATION_ORDER))
                .mapToInt(this::getOrderValue)
                .min().orElse(Integer.MAX_VALUE);
    }

    private int getOrderValue(Annotation annotation) {
        try {
            Method valueMethod = annotation.annotationType().getMethod(ORDER_VALUE);
            return (int) valueMethod.invoke(annotation);
        } catch (Exception e) {
            e.printStackTrace();
            return Integer.MAX_VALUE;
        }
    }
}