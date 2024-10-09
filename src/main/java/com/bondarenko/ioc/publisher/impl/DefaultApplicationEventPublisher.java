package com.bondarenko.ioc.publisher.impl;

import com.bondarenko.ioc.context.GenericApplicationContext;
import com.bondarenko.ioc.exception.EventHandlerException;
import com.bondarenko.ioc.publisher.ApplicationEventPublisher;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class DefaultApplicationEventPublisher implements ApplicationEventPublisher {
    private final Map<Class<?>, List<Method>> eventHandlersMap;
    private final GenericApplicationContext applicationContext;

    public DefaultApplicationEventPublisher(Map<Class<?>, List<Method>> eventHandlersMap,
                                            GenericApplicationContext applicationContext) {
        this.eventHandlersMap = eventHandlersMap;
        this.applicationContext = applicationContext;
    }

    public void publishEvent(Object event) {
        Class<?> eventClass = event.getClass();
        if (eventHandlersMap.containsKey(eventClass)) {
            List<Method> listeners = eventHandlersMap.get(eventClass);
            listeners.forEach(listener -> {
                try {
                    Class<?> classOfMethodOwner = listener.getDeclaringClass();
                    listener.invoke(applicationContext.getBean(classOfMethodOwner), event);
                } catch (Exception exception) {
                    throw new EventHandlerException("Can`t invoke method to handle event", exception);
                }
            });
        }
    }
}