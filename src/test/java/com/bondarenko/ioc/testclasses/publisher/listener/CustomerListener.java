package com.bondarenko.ioc.testclasses.publisher.listener;


import com.bondarenko.ioc.annotation.Autowired;
import com.bondarenko.ioc.annotation.Component;
import com.bondarenko.ioc.annotation.EventListener;
import com.bondarenko.ioc.publisher.ApplicationEventPublisher;
import com.bondarenko.ioc.testclasses.publisher.event.CustomerEvent;
import com.bondarenko.ioc.testclasses.publisher.event.OrderCompletedEvent;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Component
public class CustomerListener {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    final List<CustomerEvent> customerEvents = new ArrayList<>();

    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @EventListener(CustomerEvent.class)
    public void listen(CustomerEvent customerEvent) {
        customerEvents.add(customerEvent);
        applicationEventPublisher.publishEvent(new OrderCompletedEvent());
    }
}