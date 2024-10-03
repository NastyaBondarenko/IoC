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
import lombok.Setter;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Setter
@Getter
public class AnnotationBasedApplicationContext extends GenericApplicationContext {

    private Map<String, Bean> beanMap = new HashMap<>();
    private final Map<Class<?>, List<Method>> eventHandlersMap = new ConcurrentHashMap<>();
    private final DefaultApplicationEventPublisher eventPublisher;


    public AnnotationBasedApplicationContext(String... paths) {
        this(new AnnotationBeanDefinitionReader(paths));
    }

    public AnnotationBasedApplicationContext(BeanDefinitionReader definitionReader) {
        super(definitionReader);
        this.eventPublisher = new DefaultApplicationEventPublisher(eventHandlersMap, this);
        registerEventPublisher();
    }

    @Override
    protected void initContext(Map<String, BeanDefinition> beanDefinitions) {
        createBeanPostProcessors(beanDefinitions);
        beanPostProcessorsMap.put(AutowiredBeanPostProcessor.class.getSimpleName(),
                new Bean("autowiredBeanPostProcessor", new AutowiredBeanPostProcessor(groupedBeansByClass)));
        processBeanDefinitions(beanDefinitions);
        beanMap = createBeans(beanDefinitions);

        fillEventHandlersMap();

        injectValueDependencies(beanDefinitions, beanMap);

        processBeansBeforeInitialization(beanMap);
        initializeBeans(beanMap);
        processBeansAfterInitialization(beanMap);

    }

    @Override
    public void processBeansBeforeInitialization(Map<String, Bean> beanMap) {
        groupBeansByClass(beanMap);

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
            for (Method method : beanObject.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(EventListener.class)) {
                    Class<?> eventType = method.getAnnotation(EventListener.class).value();
                    eventHandlersMap.computeIfAbsent(eventType, k -> new ArrayList<>()).add(method);
                }
            }
        }
    }

    private void registerEventPublisher() {
        beanMap.put(DefaultApplicationEventPublisher.class.getSimpleName(),
                new Bean("applicationEventPublisher", eventPublisher));
    }
}