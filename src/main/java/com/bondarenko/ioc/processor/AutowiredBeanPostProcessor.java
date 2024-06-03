package com.bondarenko.ioc.processor;

import com.bondarenko.ioc.annotation.Autowired;
import com.bondarenko.ioc.annotation.Order;
import com.bondarenko.ioc.entity.Bean;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.*;


@Order(Integer.MIN_VALUE)
@AllArgsConstructor
public class AutowiredBeanPostProcessor implements BeanPostProcessor {

    private final Map<Class<?>, List<Object>> groupedBeansByClass;

    public AutowiredBeanPostProcessor() {
        this.groupedBeansByClass = new HashMap<>();
    }

    @Override
    public Object postProcessBeforeInitialization(String beanName, Bean bean) {
        Object beanObject = bean.getValue();
        Arrays.stream(beanObject.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Autowired.class))
                .forEach(field -> {
                    Class<?> type = field.getType();
                    groupedBeansByClass.getOrDefault(type, Collections.emptyList()).stream()
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
}