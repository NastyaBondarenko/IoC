package com.bondarenko.ioc.processor;

import com.bondarenko.ioc.annotation.Autowired;
import com.bondarenko.ioc.annotation.Order;
import com.bondarenko.ioc.exception.NoUniqueBeanOfTypeException;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Stream;

import static java.lang.String.format;

@Order(Integer.MIN_VALUE)
@AllArgsConstructor
public class AutowiredBeanPostProcessor implements BeanPostProcessor {

    private Map<Class<?>, List<Object>> groupedBeansByClass;

    @Override
    public Object postProcessBeforeInitialization(String beanName, Object beanValue) {
        Arrays.stream(findFields(beanValue))
                .filter(field -> field.isAnnotationPresent(Autowired.class))
                .forEach(field -> processFieldInjection(beanName, beanValue, field));
        return beanValue;
    }

    @Override
    public Object postProcessAfterInitialization(String beanName, Object beanValue) {
        return beanValue;
    }

    public void setGroupedBeansByClass(Map<Class<?>, List<Object>> groupedBeansByClass) {
        this.groupedBeansByClass = groupedBeansByClass;
    }

    private void checkForMultipleBeans(Class<?> type, String beanName) {
        Optional.ofNullable(groupedBeansByClass.get(type))
                .filter(list -> list.size() > 1)
                .ifPresent(list -> {
                    throw new NoUniqueBeanOfTypeException(format("Multiple beans of type %s in %s", type.getName(), beanName));
                });
    }

    private void processFieldInjection(String beanName, Object beanValue, Field field) {
        checkForMultipleBeans(field.getType(), beanName);
        groupedBeansByClass.getOrDefault(field.getType(), Collections.emptyList())
                .stream()
                .findFirst()
                .ifPresent(value -> injectDependencies(beanValue, field, value));
    }

    @SneakyThrows
    private void injectDependencies(Object beanValue, Field field, Object value) {
        Method method = findMethodToInjectRefDependencies(beanValue, field, value);
        method.invoke(beanValue, value);
    }

    @SneakyThrows
    private Method findMethodToInjectRefDependencies(Object beanValue, Field field, Object value) {
        Class<?> clazz = beanValue.getClass();
        String methodName = "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);

        return clazz.getDeclaredMethod(methodName, field.getType());
    }

    private Field[] findFields(Object beanValue) {
        Field[] fieldsOfSuperclass = beanValue.getClass().getSuperclass().getDeclaredFields();
        Field[] fieldsOfBeanValue = beanValue.getClass().getDeclaredFields();
        return Stream.concat(Arrays.stream(fieldsOfSuperclass), Arrays.stream(fieldsOfBeanValue)).toArray(Field[]::new);
    }
}