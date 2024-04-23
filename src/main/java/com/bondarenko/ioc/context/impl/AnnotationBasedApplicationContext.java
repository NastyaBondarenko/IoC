package com.bondarenko.ioc.context.impl;

import com.bondarenko.ioc.annotation.Autowired;
import com.bondarenko.ioc.context.GenericApplicationContext;
import com.bondarenko.ioc.entity.Bean;
import com.bondarenko.ioc.entity.BeanDefinition;
import com.bondarenko.ioc.util.reader.BeanDefinitionReader;
import com.bondarenko.ioc.util.reader.impl.AnnotationBeanDefinitionReader;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Setter
@Getter
@NoArgsConstructor
public class AnnotationBasedApplicationContext extends GenericApplicationContext {

    public AnnotationBasedApplicationContext(String... paths) {
        this(new AnnotationBeanDefinitionReader(paths));
    }

    public AnnotationBasedApplicationContext(BeanDefinitionReader definitionReader) {
        Map<String, BeanDefinition> beanDefinitions = definitionReader.getBeanDefinition();
        initContext(beanDefinitions);
    }

    public void injectRefDependencies(Map<String, BeanDefinition> beanDefinitions, Map<String, Bean> beans) {
        Map<Class<?>, List<Object>> groupedBeanByClass = getGroupedBeanByClass(beans);

        beans.values().forEach(bean -> {
            Object beanObject = bean.getValue();
            Arrays.stream(beanObject.getClass().getDeclaredFields())
                    .filter(field -> field.isAnnotationPresent(Autowired.class))
                    .forEach(field -> {
                        Class<?> type = field.getType();
                        groupedBeanByClass.getOrDefault(type, Collections.emptyList()).stream()
                                .findFirst()
                                .ifPresent(value -> super.findMethodToInjectRefDependencies(bean, field.getName(), value));
                    });
        });
    }

    private Map<Class<?>, List<Object>> getGroupedBeanByClass(Map<String, Bean> beans) {
        return beans.values().stream()
                .collect(Collectors.groupingBy(bean -> bean.getValue().getClass(),
                        Collectors.mapping(Bean::getValue, Collectors.toList())));
    }
}