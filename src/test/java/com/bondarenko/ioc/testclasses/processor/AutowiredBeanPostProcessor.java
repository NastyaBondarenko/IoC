package com.bondarenko.ioc.testclasses.processor;

import com.bondarenko.ioc.annotation.Autowired;
import com.bondarenko.ioc.entity.Bean;
import com.bondarenko.ioc.processor.BeanPostProcessor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Order;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Order(1)
public class AutowiredBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(String beanName, Bean bean, Map<String, Bean> beanMap) {
        Map<Class<?>, List<Object>> groupedBeanByClass = getGroupedBeanByClass(beanMap);
        Object beanObject = bean.getValue();
        Arrays.stream(beanObject.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Autowired.class))
                .forEach(field -> {
                    Class<?> type = field.getType();
                    groupedBeanByClass.getOrDefault(type, Collections.emptyList()).stream()
                            .findFirst()
                            .ifPresent(value -> findMethodToInjectRefDependencies(bean, field.getName(), value));
                });
        return beanObject;
    }

    @Override
    public Object postProcessAfterInitialization(String beanName, Bean bean) {
        return bean.getValue();
    }

    @SneakyThrows
    private void findMethodToInjectRefDependencies(Bean bean, String fieldName, Object value) {
        Method[] methods = bean.getValue().getClass().getMethods();
        String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                method.invoke(bean.getValue(), value);
            }
        }
    }

    private Map<Class<?>, List<Object>> getGroupedBeanByClass(Map<String, Bean> beans) {
        return beans.values().stream()
                .collect(Collectors.groupingBy(bean -> bean.getValue().getClass(),
                        Collectors.mapping(Bean::getValue, Collectors.toList())));
    }
}