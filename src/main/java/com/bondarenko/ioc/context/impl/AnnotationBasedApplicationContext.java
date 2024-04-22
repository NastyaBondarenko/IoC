package com.bondarenko.ioc.context.impl;

import com.bondarenko.ioc.annotation.Autowired;
import com.bondarenko.ioc.entity.Bean;
import com.bondarenko.ioc.entity.BeanDefinition;
import com.bondarenko.ioc.exception.BeanInstantiationException;
import com.bondarenko.ioc.processor.BeanFactoryPostProcessor;
import com.bondarenko.ioc.util.reader.BeanDefinitionReader;
import com.bondarenko.ioc.util.reader.impl.BeanDefinitionScanner;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Setter
@Getter
@NoArgsConstructor
public class AnnotationBasedApplicationContext extends GenericApplicationContext {

    private Map<String, Bean> beanMap = new HashMap<>();
    private Map<String, Bean> beanPostProcessorsMap = new HashMap<>();
    private List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();

    public AnnotationBasedApplicationContext(String... paths) {
        this(new BeanDefinitionScanner(paths));
    }

    public AnnotationBasedApplicationContext(BeanDefinitionReader definitionReader) {
        Map<String, BeanDefinition> beanDefinitions = definitionReader.getBeanDefinition();

        createBeanPostProcessors(beanDefinitions);
        processBeanDefinitions(beanDefinitions);
        beanMap = createBeans(beanDefinitions);
        injectValueDependencies(beanDefinitions, beanMap);
        injectRefDependencies(beanDefinitions, beanMap);
        processBeansBeforeInitialization(beanMap);
        initializeBeans(beanMap);
        processBeansAfterInitialization(beanMap);
    }

    @Override
    public Object getBean(String beanId) {
        return super.getBean(beanId);
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        return super.getBean(clazz);
    }

    @Override
    public <T> T getBean(String id, Class<T> clazz) {
        return super.getBean(id, clazz);
    }

    @Override
    public List<String> getBeanNames() {
        return new ArrayList<>(beanMap.keySet());
    }

    public Map<String, Bean> createBeans(Map<String, BeanDefinition> beanDefinitionMap) {
        for (Map.Entry<String, BeanDefinition> beanDefinition : beanDefinitionMap.entrySet()) {
            String className = beanDefinition.getValue().getClassName();
            String key = beanDefinition.getKey();
            try {
                Object beanObject = Class.forName(className).getDeclaredConstructor().newInstance();
                Bean bean = new Bean(key, beanObject);
                beanMap.put(key, bean);

            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException |
                     NoSuchMethodException | InvocationTargetException exception) {
                throw new BeanInstantiationException("Can`t create bean`s instantiation", exception);
            }
        }
        return beanMap;
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

    public void injectValueDependencies(Map<String, BeanDefinition> beanDefinitionMap, Map<String, Bean> beanMap) {
        super.injectValueDependencies(beanDefinitionMap, beanMap);
    }

    @SneakyThrows
    void injectValue(Object object, Method classMethod, String propertyValue) {
        super.injectValue(object, classMethod, propertyValue);
    }

    void setBeans(Map<String, Bean> beans) {
        this.beanMap = beans;
    }

    @SneakyThrows
    void createBeanPostProcessors(Map<String, BeanDefinition> beanDefinitionMap) {
        super.createBeanPostProcessors(beanDefinitionMap);
    }

    void processBeanDefinitions(Map<String, BeanDefinition> beanDefinitionsMap) {
        super.processBeanDefinitions(beanDefinitionsMap);
    }

    void processBeansBeforeInitialization(Map<String, Bean> beanMap) {
        super.processBeansBeforeInitialization(beanMap);
    }

    void initializeBeans(Map<String, Bean> beanMap) {
        super.initializeBeans(beanMap);
    }

    void processBeansAfterInitialization(Map<String, Bean> beanMap) {
        super.processBeansAfterInitialization(beanMap);
    }

    private Map<Class<?>, List<Object>> getGroupedBeanByClass(Map<String, Bean> beans) {
        return beans.values().stream()
                .collect(Collectors.groupingBy(bean -> bean.getValue().getClass(),
                        Collectors.mapping(Bean::getValue, Collectors.toList())));
    }
}