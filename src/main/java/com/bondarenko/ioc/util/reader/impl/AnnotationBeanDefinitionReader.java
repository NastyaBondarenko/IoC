package com.bondarenko.ioc.util.reader.impl;

import com.bondarenko.ioc.annotation.Component;
import com.bondarenko.ioc.entity.BeanDefinition;
import com.bondarenko.ioc.util.reader.BeanDefinitionReader;
import lombok.AllArgsConstructor;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
public class AnnotationBeanDefinitionReader implements BeanDefinitionReader {
    private String[] beanPackage;

    @Override
    public Map<String, BeanDefinition> getBeanDefinition() {
        return Arrays.stream(beanPackage)
                .flatMap(packageName -> new Reflections(
                                new ConfigurationBuilder()
                                        .forPackages(packageName)
                                        .setScanners(Scanners.SubTypes.filterResultsBy(s -> true))
                        ).getSubTypesOf(Object.class).stream()
                                .filter(clazz -> clazz.getPackage().getName().equals(packageName) &&
                                        clazz.isAnnotationPresent(Component.class))
                                .map(clazz -> new BeanDefinition(clazz.getSimpleName(), clazz.getName()))
                ).collect(Collectors.toMap(BeanDefinition::getId, beanDefinition -> beanDefinition));
    }
}