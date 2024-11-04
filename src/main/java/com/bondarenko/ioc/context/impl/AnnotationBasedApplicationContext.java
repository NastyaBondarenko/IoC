package com.bondarenko.ioc.context.impl;

import com.bondarenko.ioc.annotation.EventListener;
import com.bondarenko.ioc.annotation.Order;
import com.bondarenko.ioc.context.GenericApplicationContext;
import com.bondarenko.ioc.entity.Bean;
import com.bondarenko.ioc.entity.BeanDefinition;
import com.bondarenko.ioc.processor.AutowiredBeanPostProcessor;
import com.bondarenko.ioc.processor.BeanPostProcessor;
import com.bondarenko.ioc.publisher.impl.DefaultApplicationEventPublisher;
import com.bondarenko.ioc.util.reader.BeanDefinitionReader;
import com.bondarenko.ioc.util.reader.impl.AnnotationBeanDefinitionReader;
import lombok.Getter;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Stream;


@Getter
public class AnnotationBasedApplicationContext extends GenericApplicationContext {

    private Map<Class<?>, List<Method>> eventHandlersMap;
    private DefaultApplicationEventPublisher eventPublisher;

    public AnnotationBasedApplicationContext(String... paths) {
        this(new AnnotationBeanDefinitionReader(paths));
    }

    public AnnotationBasedApplicationContext(BeanDefinitionReader definitionReader) {
        super(definitionReader);
    }

    @Override
    protected void initContext(Map<String, BeanDefinition> beanDefinitions) {
        eventHandlersMap = new HashMap<>();
        createBeanPostProcessors(beanDefinitions);
        beanPostProcessorsMap.put(AutowiredBeanPostProcessor.class.getSimpleName(),
                new Bean("autowiredBeanPostProcessor", new AutowiredBeanPostProcessor(groupedBeansByClass)));
        processBeanDefinitions(beanDefinitions);
        beanMap = createBeans(beanDefinitions);
        registerEventPublisher(beanMap);
        injectValueDependencies(beanDefinitions, beanMap);

        processBeansBeforeInitialization(beanMap);
        initializeBeans(beanMap);
        processBeansAfterInitialization(beanMap);
    }

    @Override
    public void processBeansBeforeInitialization(Map<String, Bean> beanMap) {
        groupBeansByClass(beanMap);
        fillEventHandlersMap();

        List<Bean> beanPostProcessors = getBeanPostProcessors(beanPostProcessorsMap);
        for (Bean beanPostProcessor : beanPostProcessors) {

            BeanPostProcessor objectBeanPostProcessor = (BeanPostProcessor) beanPostProcessor.getValue();
            if (objectBeanPostProcessor instanceof AutowiredBeanPostProcessor) {
                ((AutowiredBeanPostProcessor) objectBeanPostProcessor).setGroupedBeansByClass(groupedBeansByClass);
            }
            Set<String> beanKeys = new HashSet<>(beanMap.keySet());
            for (String beanId : beanKeys) {
                Bean bean = beanMap.get(beanId);
                Object beanValue = bean.getValue();

                Object object = objectBeanPostProcessor.postProcessBeforeInitialization(beanId, beanValue);
                bean.setValue(object);
                beanMap.put(beanId, bean);
            }
        }
    }

    @Override
    public List<Bean> getBeanPostProcessors(Map<String, Bean> beanPostProcessorsMap) {
        return beanPostProcessorsMap.values().stream()
                .sorted(Comparator.comparingInt(this::getOrder))
                .toList();
    }

    private int getOrder(Bean bean) {
        Order annotation = bean.getValue().getClass().getAnnotation(Order.class);
        return Objects.nonNull(annotation) ? annotation.value() : Integer.MAX_VALUE;
    }

    private void fillEventHandlersMap() {
        for (Bean bean : beanMap.values()) {
            Object beanObject = bean.getValue();
            for (Method method : findMethods(beanObject)) {
                if (method.isAnnotationPresent(EventListener.class)) {
                    Class<?> eventType = method.getAnnotation(EventListener.class).value();
                    eventHandlersMap.computeIfAbsent(eventType, k -> new ArrayList<>()).add(method);
                }
            }
        }
    }

    private Method[] findMethods(Object beanObject) {
        Class<?> clazz = beanObject.getClass();
        Method[] declaredMethods = clazz.getDeclaredMethods();
        Method[] superclassMethods = clazz.getSuperclass().getDeclaredMethods();
        return Stream.concat(Arrays.stream(declaredMethods), Arrays.stream(superclassMethods)).toArray(Method[]::new);
    }

    private void registerEventPublisher(Map<String, Bean> beanMap) {
        this.eventPublisher = new DefaultApplicationEventPublisher(eventHandlersMap, this);
        beanMap.put(DefaultApplicationEventPublisher.class.getSimpleName(),
                new Bean("applicationEventPublisher", eventPublisher));
    }
}