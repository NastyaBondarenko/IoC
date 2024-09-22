package com.bondarenko.ioc.publisher;

public interface ApplicationEventPublisher {

    void publishEvent(Object event);
}