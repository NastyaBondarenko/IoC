package com.study.ioc.reader.sax;

import com.study.ioc.entity.BeanDefinition;
import com.study.ioc.reader.BeanDefinitionReader;
import lombok.AllArgsConstructor;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
public class BeanDefinitionScanner implements BeanDefinitionReader {
    private String[] beanPackage;

    @Override
    public Map<String, BeanDefinition> getBeanDefinition() {
        return Arrays.stream(beanPackage)
                .flatMap(packageName -> new Reflections(new ConfigurationBuilder()
                        .forPackages(packageName)
                        .filterInputsBy(new FilterBuilder().includePackage(packageName))
                        .setScanners(new SubTypesScanner(false)))
                        .getSubTypesOf(Object.class).stream()
                        .map(clazz -> new BeanDefinition(clazz.getSimpleName(), clazz.getName())))
                .collect(Collectors.toMap(BeanDefinition::getId, beanDefinition -> beanDefinition));
    }
}