package com.study.ioc.context.impl;

import com.study.ioc.context.ApplicationContext;
import com.study.ioc.entity.Bean;
import com.study.ioc.entity.BeanDefinition;
import com.study.ioc.exception.BeanInstantiationException;
import com.study.ioc.exception.NoSuchBeanDefinitionException;
import com.study.ioc.exception.NoUniqueBeanOfTypeException;
import com.study.ioc.reader.BeanDefinitionReader;
import com.study.ioc.reader.sax.XmlBeanDefinitionReader;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GenericApplicationContext implements ApplicationContext {

    private Map<String, Bean> beanMap = new HashMap<>();

    GenericApplicationContext() {
    }

    public GenericApplicationContext(String... paths) {
        this(new XmlBeanDefinitionReader(paths));
    }

    public GenericApplicationContext(BeanDefinitionReader definitionReader) {
        Map<String, BeanDefinition> beanDefinitions = definitionReader.getBeanDefinition();

        Map<String, Bean> beans = createBeans(beanDefinitions);
        injectValueDependencies(beanDefinitions, beans);
        injectRefDependencies(beanDefinitions, beans);
        this.beanMap = beans;
    }

    @Override
    public Object getBean(String beanId) {
        if (beanMap.containsKey(beanId)) {
            return beanMap.get(beanId).getValue();
        }
        return null;
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        Set<Bean> beans = new HashSet<>(beanMap.values());
        beans = beans.stream().filter(bean ->
                clazz.isAssignableFrom(bean.getValue().getClass())).collect(Collectors.toSet());
        if (beans.size() == 1) {
            return clazz.cast(beans.stream().findFirst().get().getValue());
        }
        throw new NoUniqueBeanOfTypeException("Bean is not unique");
    }

    @Override
    public <T> T getBean(String id, Class<T> clazz) {
        Class<?> beanClass = null;
        if (beanMap.containsKey(id)) {
            Bean bean = beanMap.get(id);
            beanClass = bean.getValue().getClass();
            if (Objects.equals(clazz, beanClass)) {
                return clazz.cast(bean.getValue());
            }
        }
        throw new NoSuchBeanDefinitionException(id, clazz.getName(), beanClass.getName());
    }

    @Override
    public List<String> getBeanNames() {
        return new ArrayList<>(beanMap.keySet());
    }

    Map<String, Bean> createBeans(Map<String, BeanDefinition> beanDefinitionMap) {
        Map<String, Bean> beans = beanDefinitionMap.entrySet().stream()
                .collect(Collectors.toMap(e -> String.format(e.getKey()), e -> {
                    try {
                        return new Bean(e.getKey(), Class.forName(e.getValue().getClassName())
                                .getConstructor().newInstance());
                    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException |
                             NoSuchMethodException | InvocationTargetException exception) {
                        throw new BeanInstantiationException("Can`t create bean instantiation", exception);
                    }
                }));
        beanMap.putAll(beans);
        return beanMap;
    }

    void injectValueDependencies(Map<String, BeanDefinition> beanDefinitions, Map<String, Bean> beans) {
        beans.forEach((key, value) -> {
            if (beanDefinitions.containsKey(key)) {
                Map<String, String> valueDependencies = beanDefinitions.get(key).getValueDependencies();

                Field[] fields = value.getValue().getClass().getDeclaredFields();
                for (Field field : fields) {
                    if (valueDependencies.containsKey(field.getName())) {
                        try {
                            field.setAccessible(true);
                            Class<?> fieldType = field.getType();
                            String valueDependency = valueDependencies.get(field.getName());
                            field.set(value.getValue(), getObject(valueDependency, fieldType));
                        } catch (IllegalAccessException exception) {
                            throw new BeanInstantiationException("injection valueDependencies is failed", exception);
                        }
                    }
                }
            }
        });
    }

    void injectRefDependencies(Map<String, BeanDefinition> beanDefinitions, Map<String, Bean> beans) {
        beans.forEach((key, value) -> {
            if (beanDefinitions.containsKey(key)) {
                Map<String, String> refDependencies = beanDefinitions.get(key).getRefDependencies();
                Field[] fields = value.getValue().getClass().getDeclaredFields();
                for (Field field : fields) {
                    if (refDependencies.containsKey(field.getName())) {
                        try {
                            field.setAccessible(true);
                            String refDependency = refDependencies.get(field.getName());
                            field.set(value.getValue(), beans.get(refDependency).getValue());
                        } catch (IllegalAccessException exception) {
                            throw new BeanInstantiationException("injection refDependencies is failed", exception);
                        }
                    }
                }
            }
        });
    }

    void injectValue(Object object, Method classMethod, String propertyValue) throws ReflectiveOperationException {
        Method[] declaredMethods = object.getClass().getDeclaredMethods();
        String methodName = classMethod.getName();

        List<Method> searchedMethods = Stream.of(declaredMethods)
                .filter(method -> method.getName().equals(methodName))
                .filter(method -> method.getParameterTypes().length == 1)
                .collect(Collectors.toList());

        for (Method searchedMethod : searchedMethods) {
            Class<?>[] parameterTypes = searchedMethod.getParameterTypes();
            String name = parameterTypes[0].getName();
            if (name.equalsIgnoreCase(Integer.TYPE.getName())) {
                int port = Integer.parseInt(propertyValue);
                searchedMethod.setAccessible(true);
                searchedMethod.invoke(object, port);
            }
        }
    }

    void setBeans(Map<String, Bean> beans) {
        this.beanMap = beans;
    }

    private String getSetterName(String fieldName) {
        return "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    private Object getObject(String value, Class<?> clazz) {
        if (Boolean.TYPE == clazz) return Boolean.parseBoolean(value);
        if (Byte.TYPE == clazz) return Byte.parseByte(value);
        if (Short.TYPE == clazz) return Short.parseShort(value);
        if (Integer.TYPE == clazz) return Integer.parseInt(value);
        if (Long.TYPE == clazz) return Long.parseLong(value);
        if (Float.TYPE == clazz) return Float.parseFloat(value);
        if (Double.TYPE == clazz) return Double.parseDouble(value);
        return value;
    }
}
