package com.bondarenko.ioc.publisher.impl;

import com.bondarenko.ioc.annotation.EventListener;
import com.bondarenko.ioc.context.GenericApplicationContext;
import com.bondarenko.ioc.exception.EventHandlerException;
import com.bondarenko.ioc.exception.ListenerNotFoundException;
import com.bondarenko.ioc.processor.BeanPostProcessor;
import com.bondarenko.ioc.publisher.ApplicationEventPublisher;
import lombok.Setter;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Stream;

import static java.lang.String.format;

@Setter
public class DefaultApplicationEventPublisher implements ApplicationEventPublisher, BeanPostProcessor {
    private final Map<Class<?>, List<Method>> eventHandlersMap = new HashMap<>();
    private final GenericApplicationContext applicationContext;

    public DefaultApplicationEventPublisher(GenericApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void publishEvent(Object event) {
        Class<?> eventClass = event.getClass();

        if (!eventHandlersMap.containsKey(eventClass)) {
            throw new ListenerNotFoundException(format("No listener found for event type: %s", eventClass.getName()));
        }

        List<Method> methods = eventHandlersMap.get(eventClass);
        methods.forEach(method -> {
            try {
                Class<?> classOfMethodOwner = method.getDeclaringClass();
                method.invoke(applicationContext.getBean(classOfMethodOwner), event);
            } catch (Exception exception) {
                throw new EventHandlerException("Can`t invoke method to handle event", exception);
            }
        });
    }

    @Override
    public Object postProcessBeforeInitialization(String beanName, Object bean) {

        for (Method method : findMethods(bean)) {
            if (method.isAnnotationPresent(EventListener.class)) {
                Class<?> eventType = method.getAnnotation(EventListener.class).value();
                eventHandlersMap.computeIfAbsent(eventType, k -> new ArrayList<>()).add(method);
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(String beanName, Object bean) {
        return bean;
    }


    private Method[] findMethods(Object beanObject) {
        Class<?> clazz = beanObject.getClass();
        Method[] declaredMethods = clazz.getDeclaredMethods();
        Method[] superclassMethods = clazz.getSuperclass().getDeclaredMethods();
        return Stream.concat(Arrays.stream(declaredMethods), Arrays.stream(superclassMethods)).toArray(Method[]::new);
    }
}