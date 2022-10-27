package com.study.ioc.processor;

import com.study.ioc.entity.BeanDefinition;

import java.util.List;
import java.util.Map;

public class DefaultBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    public void postProcessBeanFactory(List<BeanDefinition> beanDefinitionList) {
        beanDefinitionList.stream().filter(beanDefinition -> beanDefinition.getId().equals("userService"))
                .findFirst().ifPresent(beanDefinition ->
                        beanDefinition.setRefDependencies(Map.of("newService", "newServiceIMAP")));
    }
}